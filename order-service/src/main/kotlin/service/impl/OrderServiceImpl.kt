package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.OrderStatus.*
import com.buoyancy.common.model.mapper.OrderMapper
import com.buoyancy.common.repository.OrderRepository
import com.buoyancy.common.utils.find
import com.buoyancy.order.messaging.producer.OrderTemplate
import com.buoyancy.order.service.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization
import java.util.*

@Service
class OrderServiceImpl : OrderService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var self: OrderService
    @Autowired
    private lateinit var repo: OrderRepository
    @Autowired
    private lateinit var kafka: OrderTemplate
    @Autowired
    private lateinit var messages: MessageSource
    @Autowired
    private lateinit var mapper: OrderMapper

    @Transactional
    @CachePut(CacheNames.ORDERS, key = "#result.id")
    override fun createOrder(orderDto: OrderDto): OrderDto {
        if (orderDto.id != null && repo.existsById(orderDto.id!!)) {
            val conflictMessage = messages.find("exceptions.conflict.suborder", orderDto.id!!)
            throw ConflictException(conflictMessage)
        }

        log.info { "Creating order for user ${orderDto.userId}" }
        val order = mapper.toEntity(orderDto).apply { status = CREATED }
        val saved = withHandling { repo.save(order) }

        afterCommit {
            kafka.sendOrderEvent(OrderEvent(saved, CREATED))
            log.info { "Order for user ${saved.user.id} created and message sent: $saved" }
        }
        return mapper.toDto(saved)
    }

    @CachePut(CacheNames.ORDERS, key = "#id")
    @Transactional
    override fun updateStatus(id: UUID, newStatus: OrderStatus): OrderDto {
        val order = self.getOrderEntity(id)
        val updated = order
        val currentStatus = order.status
        if (currentStatus != newStatus) {
            log.info { "Updating status of order $id from $currentStatus to $newStatus" }
            order.status = newStatus
            repo.save(order)
            afterCommit {
                kafka.sendOrderEvent(OrderEvent(order, newStatus))
                log.info { "Updated status of order $id to $newStatus" }
            }
        }
        return mapper.toDto(updated)
    }

    override fun getStatus(id: UUID): OrderStatus {
        return self.getOrder(id).status
    }

    @CachePut(CacheNames.ORDERS, key = "#id")
    override fun cancelOrder(id: UUID): OrderDto {
        if (getStatus(id) == READY)
            throw BadRequestException(messages.find("exceptions.bad-request.order.cancel", id))
        return self.updateStatus(id, CANCELLED)
    }

    @Transactional
    @Cacheable(CacheNames.ORDERS)
    override fun getOrder(id: UUID): OrderDto {
        return mapper.toDto(self.getOrderEntity(id))
    }

    @Transactional
    @Cacheable(CacheNames.ORDERS_COLLECTION)
    override fun getOrders(pageable: Pageable): Page<OrderDto> {
        return repo.findAll(pageable).map { mapper.toDto(it) }
    }

    @CacheEvict(CacheNames.ORDERS, key = "#id")
    override fun deleteOrder(id: UUID) {
        log.info { "Deleting order $id" }
        repo.deleteById(id)
    }

    override fun getOrderEntity(id: UUID): Order {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.find("exceptions.not-found.order", id))
        }
    }

    @CachePut(CacheNames.ORDERS, key = "#id")
    @Transactional
    override fun updateOrder(id: UUID, update: OrderDto): OrderDto {
        val order = self.getOrderEntity(id)
        val updated = mapper.toEntity(update).apply { this.id = id }
        log.info { "Updating order ${order.id} to $order" }
        return withHandling {
            mapper.toDto(repo.save(updated))
        }
    }

    private fun afterCommit(action: () -> Unit) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() { action() }
        })
    }

    private fun <T> withHandling(block: () -> T): T {
        return try {
            block()
        } catch (_: EntityNotFoundException) {
            throw NotFoundException(messages.find("exceptions.psql.foreign-key"))
        } catch (_: DataIntegrityViolationException) {
            throw BadRequestException(messages.find("exceptions.psql.integrity"))
        }
    }
}