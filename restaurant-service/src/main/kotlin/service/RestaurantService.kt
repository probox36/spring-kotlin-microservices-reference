package com.buoyancy.restaurant.service

import com.buoyancy.common.model.entity.Restaurant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface RestaurantService {
    fun createRestaurant(restaurant: Restaurant): Restaurant
    fun getRestaurants(pageable: Pageable): Page<Restaurant>
    fun getRestaurant(id: UUID): Restaurant
    fun updateRestaurant(id: UUID, restaurant: Restaurant): Restaurant
    fun deleteRestaurant(id: UUID)
    fun updateName(id: UUID, name: String): Restaurant
    fun updateAddress(id: UUID, address: String): Restaurant
    fun updatePhone(id: UUID, phone: String): Restaurant
    fun updateEmail(id: UUID, email: String): Restaurant
}