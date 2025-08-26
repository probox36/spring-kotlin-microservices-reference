package com.buoyancy.order.service

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus
import java.util.*

interface OrderService {

    fun createOrder(order: OrderDto): OrderDto
    fun updateStatus(id: UUID, status: OrderStatus): OrderDto
    fun getStatus(id: UUID): OrderStatus
    fun cancelOrder(id: UUID): OrderDto
    fun getOrder(id: UUID): OrderDto
    fun getOrderEntity(id: UUID): Order
    fun updateOrder(id: UUID, updatedOrder: OrderDto): OrderDto
}