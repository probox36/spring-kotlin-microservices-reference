package com.buoyancy.order.controller

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.utils.get
import com.buoyancy.order.service.impl.SuborderServiceImpl
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
@RequestMapping("/suborders")
class SuborderController {

    @Autowired
    private lateinit var service: SuborderServiceImpl
    @Autowired
    private lateinit var messages : MessageSource

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping()
    fun getSuborders(pageable: Pageable): Page<SuborderDto> {
        return service.getSuborders(pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/by-restaurant/{restaurantId}")
    fun getSubordersByRestaurant(@PathVariable restaurantId: UUID, @RequestParam status: SuborderStatus?, pageable: Pageable): Page<SuborderDto> {
        return status?.let { service.getSubordersByRestaurantIdAndStatus(restaurantId, it, pageable) }
            ?: service.getSubordersByRestaurantId(restaurantId, pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/{id}")
    fun getSuborder(@PathVariable id: UUID): SuborderDto {
        return service.getSuborder(id)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createSuborder(@Valid @RequestBody suborderDto: SuborderDto): ResourceDto<SuborderDto> {
        val created = service.createSuborder(suborderDto)
        val message = messages.get("rest.response.suborders.created", created.id!!)
        return ResourceDto(201, message, created)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    fun deleteSuborder(@PathVariable id: UUID): MessageDto {
        service.deleteSuborder(id)
        val message = messages.get("rest.response.resource.deleted", id)
        return MessageDto(200, message)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/update")
    fun updateSuborder(@Valid @RequestBody orderDto: SuborderDto): ResourceDto<SuborderDto> {
        val updated = orderDto.id?.let { service.updateSuborder(it, orderDto) }
            ?: throw BadRequestException(messages.get("exceptions.bad-request.order.null-id"))
        val message = messages.get("rest.response.suborders.updated", orderDto.id!!)
        return ResourceDto(200, message, updated)
    }
}