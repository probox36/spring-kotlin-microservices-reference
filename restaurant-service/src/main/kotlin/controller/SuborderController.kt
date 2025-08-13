package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.mapper.SuborderMapper
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.SuborderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/suborders")
class SuborderController {

    @Autowired
    lateinit var service: SuborderService
    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var mapper: SuborderMapper

    @GetMapping()
    fun getSuborders(pageable: Pageable): Page<SuborderDto> {
        return service.getSuborders(pageable).map { mapper.toDto(it) }
    }

    @PostMapping("/{id}/accept")
    fun acceptOrder(@PathVariable id: UUID): MessageDto {
        service.markSuborderAsPreparing(id)
        val message = messages.get("rest.response.suborders.preparing", id)
        return MessageDto(200, message)
    }

    @PostMapping("/{id}/postpone")
    fun postponeOrderPreparation(@PathVariable id: UUID, @RequestParam("reason") reason: String): MessageDto {
        service.postponeSuborder(id)
        val message = messages.get("rest.response.suborders.postponed", id)
        return MessageDto(200, message)
    }

    @PostMapping("/{id}/finish")
    fun finishOrderPreparation(@PathVariable id: UUID): MessageDto {
        service.markSuborderAsReady(id)
        val message = messages.get("rest.response.suborders.finished", id)
        return MessageDto(200, message)
    }

    @GetMapping("/restaurant/{restaurantId}")
    fun getSubordersByRestaurant(@PathVariable restaurantId: UUID, pageable: Pageable): Page<SuborderDto> {
        return service.getSubordersByRestaurant(restaurantId, pageable).map { mapper.toDto(it) }
    }

    @GetMapping("/{id}")
    fun getSuborder(@PathVariable id: UUID): SuborderDto {
        return mapper.toDto(service.getSuborder(id))
    }
}