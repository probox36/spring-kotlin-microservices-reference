package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.mapper.OrderMapper
import com.buoyancy.restaurant.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/orders")
class RestaurantController {

    @Autowired
    lateinit var orderRepository: OrderRepository
    @Autowired
    lateinit var orderMapper: OrderMapper

    @GetMapping()
    fun getOrders(pageable: Pageable): Page<OrderDto> {
        return orderRepository.findAll(pageable).map { orderMapper.toDto(it) }
    }

    @PostMapping("/{id}/accept")
    fun acceptOrder(@PathVariable id: UUID) {

    }

    @PostMapping("/{id}/reject")
    fun rejectOrder(@PathVariable id: UUID, @RequestParam("reason") reason: String) {

    }

    @PostMapping("/{id}/postpone")
    fun postponeOrderPreparation(@PathVariable id: UUID, @RequestParam("reason") reason: String) {

    }

    @PostMapping("/{id}/finish")
    fun finishOrderPreparation() {

    }

}