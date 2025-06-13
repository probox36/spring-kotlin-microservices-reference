package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.order.repository.OrderRepository
import com.buoyancy.order.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderServiceImpl : OrderService {

    @Autowired
    private lateinit var orderRepository: OrderRepository
    override fun createOrder(order: Order): Order {
        return orderRepository.save(order)
    }

    override fun updateStatus(id: UUID, status: OrderStatus) {
        val order = getOrder(id)
        order.status = status
        orderRepository.save(order)
    }

    override fun getStatus(id: UUID): OrderStatus {
        return getOrder(id).status
    }

    override fun cancelOrder(id: UUID) {
        updateStatus(id, OrderStatus.CANCELLED)
    }

    override fun getOrder(id: UUID): Order {
        return orderRepository.findById(id).orElseThrow {
            NotFoundException("Order with id $id not found")
        }
    }
}