package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.entity.Order
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
        order.status = OrderStatus.CREATED
        val savedOrder = repo.save(order)
        afterCommit {
            kafka.sendOrderEvent(OrderEvent(savedOrder, OrderStatus.CREATED))
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
        self.updateStatus(id, OrderStatus.CANCELLED)
    }

    override fun getOrder(id: UUID): Order {
        return repo.findById(id).orElseThrow {
            NotFoundException("Order with id $id not found")
        }
    }

    override fun closeOrder(id: UUID) {
        val order = getOrder(id)
        when (order.status) {
            OrderStatus.READY -> self.updateStatus(id, OrderStatus.READY)
            else -> throw BadRequestException("Order with id $id is not ready yet")
        }
    }

    @Transactional
    override fun updateOrder(id: UUID, update: Order) {
        val order = getOrder(id)
        order.user = update.user
        order.status = update.status
        order.items = update.items
        log.info { "Updated order ${order.id} to $order" }
        repo.save(order)
    }

    private fun afterCommit(action: () -> Unit) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() { action() }
        })
    }
}