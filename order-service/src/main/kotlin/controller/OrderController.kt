package com.buoyancy.order.controller

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.mapper.OrderMapper
import com.buoyancy.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController {

    @Autowired
    lateinit var orderService: OrderService
    @Autowired
    lateinit var orderMapper: OrderMapper

    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody orderDto: OrderDto): ResponseEntity<OrderDto> {
        val order = orderMapper.toEntity(orderDto)
        val createdOrderId = orderService.createOrder(order)
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toDto(createdOrderId))
    }

    @GetMapping("/{id}/status")
    fun getOrderStatus(@PathVariable id: UUID): ResponseEntity<OrderStatus> {
        val orderStatus = orderService.getStatus(id)
        return ResponseEntity.ok(orderStatus)
    }

    @PutMapping("/{id}/cancel")
    fun cancelOrder(@PathVariable id: UUID): ResponseEntity<Unit> {
        orderService.cancelOrder(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/close")
    fun closeOrder(@PathVariable id: UUID): ResponseEntity<Unit> {
        orderService.cancelOrder(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: UUID): ResponseEntity<OrderDto> {
        val order = orderService.getOrder(id)
        return ResponseEntity.ok(orderMapper.toDto(order))
    }

    @PostMapping("/{id}/update")
    fun updateOrder(@Valid @RequestBody orderDto: OrderDto): ResponseEntity<OrderDto> {
        val order = orderMapper.toEntity(orderDto)
        order.id.let { orderService.updateOrder(it, order) }
        return ResponseEntity.ok(orderMapper.toDto(order))
    }
}