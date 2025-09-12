package com.buoyancy.order.controller

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.utils.find
import com.buoyancy.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController {

    @Autowired
    private lateinit var service: OrderService
    @Autowired
    private lateinit var messages : MessageSource

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createOrder(@Valid @RequestBody orderDto: OrderDto): ResourceDto<OrderDto> {
        val created = service.createOrder(orderDto)
        val message = messages.find("rest.response.orders.created", created.id!!)
        return ResourceDto(201, message, created)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/{id}/status")
    fun getOrderStatus(@PathVariable id: UUID): OrderStatus {
        val orderStatus = service.getStatus(id)
        return orderStatus
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping("/{id}/cancel")
    fun cancelOrder(@PathVariable id: UUID): MessageDto {
        service.cancelOrder(id)
        return MessageDto(200, messages.find("rest.response.orders.cancelled", id))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: UUID): OrderDto {
        return service.getOrder(id)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping()
    fun getOrders(pageable: Pageable): Page<OrderDto> {
        return service.getOrders(pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @DeleteMapping("/{id}/delete")
    fun deleteOrder(@PathVariable id: UUID): MessageDto {
        service.deleteOrder(id)
        val message = messages.find("rest.response.resource.deleted", id)
        return MessageDto(200, message)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/update")
    fun updateOrder(@Valid @RequestBody orderDto: OrderDto): ResourceDto<OrderDto> {
        val updated = orderDto.id?.let { service.updateOrder(it, orderDto) }
            ?: throw BadRequestException(messages.find("exceptions.bad-request.order.null-id"))
        val message = messages.find("rest.response.orders.updated", orderDto.id!!)
        return ResourceDto(200, message, updated)
    }
}