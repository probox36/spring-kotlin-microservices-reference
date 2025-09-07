package com.buoyancy.order.controller

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.utils.get
import com.buoyancy.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController {

    @Autowired
    private lateinit var service: OrderService
    @Autowired
    private lateinit var messages : MessageSource

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody orderDto: OrderDto): ResourceDto<OrderDto> {
        val created = service.createOrder(orderDto)
        val message = messages.get("rest.response.orders.created", created.id!!)
        return ResourceDto(201, message, created)
    }

    @GetMapping("/{id}/status")
    fun getOrderStatus(@PathVariable id: UUID): OrderStatus {
        val orderStatus = service.getStatus(id)
        return orderStatus
    }

    @PutMapping("/{id}/cancel")
    fun cancelOrder(@PathVariable id: UUID): MessageDto {
        service.cancelOrder(id)
        return MessageDto(200, messages.get("rest.response.orders.cancelled", id))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: UUID): OrderDto {
        return service.getOrder(id)
    }

    @GetMapping()
    fun getOrders(pageable: Pageable): Page<OrderDto> {
        return service.getOrders(pageable)
    }

    @DeleteMapping("/{id}/delete")
    fun deleteOrder(@PathVariable id: UUID): MessageDto {
        service.deleteOrder(id)
        val message = messages.get("rest.response.resource.deleted", id)
        return MessageDto(200, message)
    }

    @PostMapping("/{id}/update")
    fun updateOrder(@Valid @RequestBody orderDto: OrderDto): ResourceDto<OrderDto> {
        val updated = orderDto.id?.let { service.updateOrder(it, orderDto) }
            ?: throw BadRequestException(messages.get("exceptions.bad-request.order.null-id"))
        val message = messages.get("rest.response.orders.updated", orderDto.id!!)
        return ResourceDto(200, message, updated)
    }
}