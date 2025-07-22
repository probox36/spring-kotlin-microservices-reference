package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.RestaurantDto
import com.buoyancy.common.model.dto.rest.EmailUpdateDto
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.mapper.RestaurantMapper
import com.buoyancy.restaurant.service.RestaurantService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/restaurants")
class RestaurantController {

    @Autowired
    private lateinit var service: RestaurantService
    @Autowired
    private lateinit var mapper: RestaurantMapper

    @GetMapping()
    fun getRestaurants(pageable: Pageable): Page<Restaurant> {
        return service.getRestaurants(pageable)
    }

    @GetMapping("/{id}")
    fun getRestaurant(@PathVariable id: UUID): Restaurant {
        return service.getRestaurant(id)
    }

    @PostMapping("/{id}/update")
    fun updateRestaurant(@PathVariable id: UUID, @Valid @RequestBody restaurantDto: RestaurantDto) {
        val restaurant = mapper.toEntity(restaurantDto)
        service.updateRestaurant(id, restaurant)
    }

    @DeleteMapping("/{id}/delete")
    fun deleteRestaurant(@PathVariable id: UUID) {
        service.deleteRestaurant(id)
    }

    @PostMapping("/{id}/updateName")
    fun updateName(@PathVariable id: UUID, @RequestBody name: String) {
        service.updateName(id, name)
    }

    @PostMapping("/{id}/updateAddress")
    fun updateAddress(@PathVariable id: UUID, @RequestBody address: String) {
        service.updateAddress(id, address)
    }

    @PostMapping("/{id}/updatePhone")
    fun updatePhone(@PathVariable id: UUID, @RequestBody phone: String) {
        service.updatePhone(id, phone)
    }

    @PostMapping("/{id}/updateEmail")
    fun updateEmail(@PathVariable id: UUID, @RequestBody dto: EmailUpdateDto) {
        service.updateEmail(id, dto.email)
    }
}