package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus.*
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.repository.OrderRepository
import com.buoyancy.common.utils.get
import com.buoyancy.order.messaging.producer.OrderTemplate
import com.buoyancy.order.service.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
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

    @Transactional
    override fun createOrder(order: Order): Order {
        if (order.id != null && repo.existsById(order.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.suborder", order.id!!)
            throw ConflictException(conflictMessage)
        }
        log.info { "Creating order $order for user ${order.user.id}" }
        order.status = CREATED
        val savedOrder = repo.save(order)
        afterCommit {
            kafka.sendOrderEvent(OrderEvent(savedOrder, CREATED))
            log.info { "Order for user ${savedOrder.user.id} created and message sent: $savedOrder" }
        }
        return savedOrder
    }
    @Transactional
    override fun updateStatus(id: UUID, newStatus: OrderStatus): Order {
        val order = getOrder(id)
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
        return updated
    }

    override fun getStatus(id: UUID): OrderStatus {
        return getOrder(id).status
    }

    override fun cancelOrder(id: UUID) {
        if (getStatus(id) == READY)
            throw BadRequestException(messages.get("exceptions.bad-request.order.cancel", id))
        self.updateStatus(id, CANCELLED)
    }

    override fun getOrder(id: UUID): Order {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.get("exceptions.not-found.order", id))
        }
    }

    @Transactional
    override fun updateOrder(id: UUID, updated: Order): Order {
        val order = getOrder(id)
        order.user = updated.user
        order.status = updated.status
        order.items = updated.items
        log.info { "Updated order ${order.id} to $order" }
        return repo.save(order)
    }

    private fun afterCommit(action: () -> Unit) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() { action() }
        })
    }
}