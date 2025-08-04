package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.utils.get
import com.buoyancy.order.messaging.producer.OrderTemplate
import com.buoyancy.order.repository.OrderRepository
import com.buoyancy.order.service.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderServiceImpl : OrderService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var repo: OrderRepository
    @Autowired
    private lateinit var kafka: OrderTemplate
    @Autowired
    private lateinit var messages: MessageSource

    override fun createOrder(order: Order): Order {
        if (order.id != null && repo.existsById(order.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.suborder", order.id!!)
            throw ConflictException(conflictMessage)
        }
        log.info { "Creating order for user ${order.user}" }
        order.status = OrderStatus.CREATED
        val savedOrder = repo.save(order)
        kafka.sendOrderEvent(OrderEvent(savedOrder, OrderStatus.CREATED))
        log.info { "Order ${savedOrder.id} for user ${savedOrder.user} created" }
        return savedOrder
        // TODO: Remove double conversion (DTO -> Entity -> DTO)
    }

    override fun updateStatus(id: UUID, status: OrderStatus): Order {
        val order = getOrder(id)
        val updated = order
        if (order.status != status) {
            log.info { "Updating status of order $id from ${getStatus(id)} to $status" }
            order.status = status
            repo.save(order)
            kafka.sendOrderEvent(OrderEvent(order, status))
            log.info { "Updated status of order $id from ${getStatus(id)} to $status" }
        }
        return updated
    }

    override fun getStatus(id: UUID): OrderStatus {
        return getOrder(id).status
    }

    override fun cancelOrder(id: UUID) {
        updateStatus(id, OrderStatus.CANCELLED)
    }

    override fun getOrder(id: UUID): Order {
        return repo.findById(id).orElseThrow {
            NotFoundException("Order with id $id not found")
        }
    }

    override fun closeOrder(id: UUID) {
        val order = getOrder(id)
        when (order.status) {
            OrderStatus.READY -> updateStatus(id, OrderStatus.CLOSED)
            else -> throw BadRequestException("Order with id $id is not ready yet")
        }
    }

    override fun updateOrder(id: UUID, update: Order) {
        val order = getOrder(id)
        order.user = update.user
        order.status = update.status
        order.items = update.items
        log.info { "Updated order ${order.id} to $order" }
        repo.save(order)
    }
}