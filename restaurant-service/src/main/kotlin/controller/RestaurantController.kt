package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.RestaurantDto
import com.buoyancy.common.model.dto.rest.EmailUpdateDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.utils.find
import com.buoyancy.restaurant.service.RestaurantService
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
class RestaurantController {

    @Autowired
    private lateinit var service: RestaurantService
    @Autowired
    private lateinit var messages : MessageSource

    private val updatedMessage by lazy { messages.find("rest.response.resource.updated") }
    private val createdMessage by lazy { messages.find("rest.response.resource.created") }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRestaurant(restaurantDto: RestaurantDto): ResourceDto<RestaurantDto> {
        val created = service.createRestaurant(restaurantDto)
        return ResourceDto(201, createdMessage, created)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping()
    fun getRestaurants(pageable: Pageable): Page<RestaurantDto> {
        return service.getRestaurants(pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/{id}")
    fun getRestaurant(@PathVariable id: UUID): RestaurantDto {
        return service.getRestaurant(id)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/update")
    fun updateRestaurant(
        @PathVariable id: UUID,
        @Valid @RequestBody restaurantDto: RestaurantDto
    ): ResourceDto<RestaurantDto> {
        val updated = service.updateRestaurant(id, restaurantDto)
        return ResourceDto(200, updatedMessage, updated)
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #id.toString() == authentication.principal.subject")
    @DeleteMapping("/{id}/delete")
    fun deleteRestaurant(@PathVariable id: UUID): MessageDto {
        service.deleteRestaurant(id)
        val deletedMessage = messages.find("rest.response.resource.deleted", id)
        return MessageDto(200, deletedMessage)
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #id.toString() == authentication.principal.subject")
    @PostMapping("/{id}/update-name")
    fun updateName(@PathVariable id: UUID, @RequestParam name: String): ResourceDto<RestaurantDto> {
        val entity = service.updateName(id, name)
        return ResourceDto(200, updatedMessage, entity)
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #id.toString() == authentication.principal.subject")
    @PostMapping("/{id}/update-address")
    fun updateAddress(@PathVariable id: UUID, @RequestParam address: String): ResourceDto<RestaurantDto> {
        val entity = service.updateAddress(id, address)
        return ResourceDto(200, updatedMessage, entity)
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #id.toString() == authentication.principal.subject")
    @PostMapping("/{id}/update-phone")
    fun updatePhone(@PathVariable id: UUID, @RequestParam phone: String): ResourceDto<RestaurantDto> {
        val entity = service.updatePhone(id, phone)
        return ResourceDto(200, updatedMessage, entity)
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #id.toString() == authentication.principal.subject")
    @PostMapping("/{id}/update-email")
    fun updateEmail(@PathVariable id: UUID, @RequestParam dto: EmailUpdateDto): ResourceDto<RestaurantDto> {
        val entity = service.updateEmail(id, dto.email)
        return ResourceDto(200, updatedMessage, entity)
    }
}