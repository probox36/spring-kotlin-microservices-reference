package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.SuborderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/suborders")
class SuborderController {

    @Autowired
    lateinit var service: SuborderService
    @Autowired
    private lateinit var messages : MessageSource

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @PostMapping("/{id}/accept")
    fun acceptOrder(@PathVariable id: UUID): MessageDto {
        service.markSuborderAsPreparing(id)
        val message = messages.get("rest.response.suborders.preparing", id)
        return MessageDto(200, message)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @PostMapping("/{id}/postpone")
    fun postponeOrderPreparation(@PathVariable id: UUID, @RequestParam("reason") reason: String): MessageDto {
        service.postponeSuborder(id)
        val message = messages.get("rest.response.suborders.postponed", id)
        return MessageDto(200, message)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @PostMapping("/{id}/finish")
    fun finishOrderPreparation(@PathVariable id: UUID): MessageDto {
        service.markSuborderAsReady(id)
        val message = messages.get("rest.response.suborders.finished", id)
        return MessageDto(200, message)
    }
}