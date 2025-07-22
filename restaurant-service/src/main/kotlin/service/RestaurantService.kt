package com.buoyancy.restaurant.service

import com.buoyancy.common.model.entity.Restaurant
import org.springframework.data.domain.Pageable
import java.util.UUID

interface RestaurantService {
    fun getRestaurants(pageable: Pageable): List<Restaurant>
    fun getRestaurant(id: UUID): Restaurant
    fun updateRestaurant(id: UUID, restaurant: Restaurant)
    fun deleteRestaurant(id: UUID)
    fun updateName(id: UUID, name: String)
    fun updateAddress(id: UUID, address: String)
    fun updatePhone(id: UUID, phone: String)
    fun updateEmail(id: UUID, email: String)
}