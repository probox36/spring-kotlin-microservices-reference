package com.buoyancy.order.controller

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.model.dto.OrderDto
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/suborders")
class SuborderController {

    @Autowired
    private lateinit var service: SuborderServiceImpl
    @Autowired
    private lateinit var messages : MessageSource

    @GetMapping()
    fun getSuborders(pageable: Pageable): Page<SuborderDto> {
        return service.getSuborders(pageable)
    }
    @GetMapping("/by-restaurant/{restaurantId}")
    fun getSubordersByRestaurant(@PathVariable restaurantId: UUID, @RequestParam status: SuborderStatus?, pageable: Pageable): Page<SuborderDto> {
        return status?.let { service.getSubordersByRestaurantIdAndStatus(restaurantId, it, pageable) }
            ?: service.getSubordersByRestaurantId(restaurantId, pageable)
    }
    @GetMapping("/{id}")
    fun getSuborder(@PathVariable id: UUID): SuborderDto {
        return service.getSuborder(id)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createSuborder(@Valid @RequestBody suborderDto: SuborderDto): ResourceDto<SuborderDto> {
        val created = service.createSuborder(suborderDto)
        val message = messages.get("rest.response.suborders.created", created.id!!)
        return ResourceDto(201, message, created)
    }

    @DeleteMapping("/{id}/delete")
    fun deleteSuborder(@PathVariable id: UUID): MessageDto {
        service.deleteSuborder(id)
        val message = messages.get("rest.response.resource.deleted", id)
        return MessageDto(200, message)
    }

    @PostMapping("/{id}/update")
    fun updateSuborder(@Valid @RequestBody orderDto: SuborderDto): ResourceDto<SuborderDto> {
        val updated = orderDto.id?.let { service.updateSuborder(it, orderDto) }
            ?: throw BadRequestException(messages.get("exceptions.bad-request.order.null-id"))
        val message = messages.get("rest.response.suborders.updated", orderDto.id!!)
        return ResourceDto(200, message, updated)
    }
}