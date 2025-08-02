package com.buoyancy.order.controller

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.mapper.OrderMapper
import com.buoyancy.common.utils.get
import com.buoyancy.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController {

    @Autowired
    lateinit var orderService: OrderService
    @Autowired
    lateinit var orderMapper: OrderMapper
    @Autowired
    private lateinit var messages : MessageSource

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody orderDto: OrderDto): OrderDto {
        val order = orderMapper.toEntity(orderDto)
        val createdOrderId = orderService.createOrder(order)
        return orderMapper.toDto(createdOrderId)
    }

    @GetMapping("/{id}/status")
    fun getOrderStatus(@PathVariable id: UUID): OrderStatus {
        val orderStatus = orderService.getStatus(id)
        return orderStatus
    }

    @PutMapping("/{id}/cancel")
    fun cancelOrder(@PathVariable id: UUID): MessageDto {
        orderService.cancelOrder(id)
        return MessageDto(200, messages.get("rest.order.status.cancelled"))
    }

    @PutMapping("/{id}/close")
    fun closeOrder(@PathVariable id: UUID): MessageDto {
        orderService.cancelOrder(id)
        return MessageDto(200, messages.get("rest.order.status.closed"))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: UUID): OrderDto {
        val order = orderService.getOrder(id)
        return orderMapper.toDto(order)
    }

    @PostMapping("/{id}/update")
    fun updateOrder(@Valid @RequestBody orderDto: OrderDto): ResourceDto {
        val order = orderMapper.toEntity(orderDto)
        order.id?.let { orderService.updateOrder(it, order) }
        return ResourceDto(200, messages.get("rest.order.status.updated"), order)
    }
}