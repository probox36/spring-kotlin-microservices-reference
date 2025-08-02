package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.RestaurantDto
import com.buoyancy.common.model.dto.rest.EmailUpdateDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.mapper.RestaurantMapper
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.RestaurantService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/restaurants")
class RestaurantController {

    @Autowired
    private lateinit var service: RestaurantService
    @Autowired
    private lateinit var mapper: RestaurantMapper
    @Autowired
    private lateinit var messages : MessageSource

    private val updatedMessage = messages.get("rest.response.resource.updated")
    private val createdMessage = messages.get("rest.response.resource.created")

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRestaurant(restaurantDto: RestaurantDto): ResourceDto {
        val restaurant = mapper.toEntity(restaurantDto)
        return ResourceDto(201, createdMessage, service.createRestaurant(restaurant))
    }

    @GetMapping()
    fun getRestaurants(pageable: Pageable): Page<Restaurant> {
        return service.getRestaurants(pageable)
    }

    @GetMapping("/{id}")
    fun getRestaurant(@PathVariable id: UUID): Restaurant {
        return service.getRestaurant(id)
    }

    @PostMapping("/{id}/update")
    fun updateRestaurant(@PathVariable id: UUID, @Valid @RequestBody restaurantDto: RestaurantDto): ResourceDto {
        val restaurant = mapper.toEntity(restaurantDto)
        service.updateRestaurant(id, restaurant)
        return ResourceDto(200, updatedMessage, restaurant)
    }

    @DeleteMapping("/{id}/delete")
    fun deleteRestaurant(@PathVariable id: UUID): MessageDto {
        service.deleteRestaurant(id)
        val deletedMessage = messages.get("rest.response.resource.deleted", id)
        return MessageDto(200, deletedMessage)
    }

    @PostMapping("/{id}/updateName")
    fun updateName(@PathVariable id: UUID, @RequestBody name: String): ResourceDto {
        service.updateName(id, name)
        return ResourceDto(200, updatedMessage, name)
    }

    @PostMapping("/{id}/updateAddress")
    fun updateAddress(@PathVariable id: UUID, @RequestBody address: String): ResourceDto {
        return ResourceDto(200, updatedMessage, service.updateAddress(id, address))
    }

    @PostMapping("/{id}/updatePhone")
    fun updatePhone(@PathVariable id: UUID, @RequestBody phone: String): ResourceDto {
        return ResourceDto(200, updatedMessage, service.updatePhone(id, phone))
    }

    @PostMapping("/{id}/updateEmail")
    fun updateEmail(@PathVariable id: UUID, @RequestBody dto: EmailUpdateDto): ResourceDto {
        return ResourceDto(200, updatedMessage, service.updateEmail(id, dto.email))
    }
}