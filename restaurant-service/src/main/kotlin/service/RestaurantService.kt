package com.buoyancy.restaurant.service

import com.buoyancy.common.model.dto.RestaurantDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface RestaurantService {
    fun createRestaurant(restaurant: RestaurantDto): RestaurantDto
    fun getRestaurants(pageable: Pageable): Page<RestaurantDto>
    fun getRestaurant(id: UUID): RestaurantDto
    fun updateRestaurant(id: UUID, restaurant: RestaurantDto): RestaurantDto
    fun deleteRestaurant(id: UUID)
    fun updateName(id: UUID, name: String): RestaurantDto
    fun updateAddress(id: UUID, address: String): RestaurantDto
    fun updatePhone(id: UUID, phone: String): RestaurantDto
    fun updateEmail(id: UUID, email: String): RestaurantDto
}