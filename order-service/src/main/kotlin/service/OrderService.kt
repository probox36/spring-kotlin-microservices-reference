package com.buoyancy.order.service

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus
import java.util.*

interface OrderService {

    fun createOrder(order: Order): Order
    fun updateStatus(id: UUID, status: OrderStatus)
    fun getStatus(id: UUID): OrderStatus
    fun cancelOrder(id: UUID)
    fun getOrder(id: UUID): Order
    fun getOrderById(id: UUID): Order?
    fun updateOrder(id: UUID, updatedOrder: Order)

}