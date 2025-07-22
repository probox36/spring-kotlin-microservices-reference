package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.restaurant.repository.RestaurantRepository
import com.buoyancy.restaurant.service.RestaurantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RestaurantServiceImpl : RestaurantService {

    @Autowired
    private lateinit var repo: RestaurantRepository

    override fun getRestaurants(pageable: Pageable): List<Restaurant> {
        return repo.findAll(pageable).content
    }

    override fun getRestaurant(id: UUID): Restaurant {
        return repo.findById(id).orElseThrow {
            NotFoundException("Restaurant with id $id not found")
        }
    }

    override fun updateRestaurant(id: UUID, restaurant: Restaurant) {
        restaurant.id = id
        repo.save(restaurant)
    }

    override fun deleteRestaurant(id: UUID) {
        repo.deleteById(id)
    }

    override fun updateName(id: UUID, name: String) {
        val restaurant = getRestaurant(id)
        restaurant.name = name
        repo.save(restaurant)
    }

    override fun updateAddress(id: UUID, address: String) {
        val restaurant = getRestaurant(id)
        restaurant.address = address
        repo.save(restaurant)
    }

    override fun updatePhone(id: UUID, phone: String) {
        val restaurant = getRestaurant(id)
        restaurant.phoneNumber = phone
        repo.save(restaurant)
    }

    override fun updateEmail(id: UUID, email: String) {
        val restaurant = getRestaurant(id)
        restaurant.email = email
        repo.save(restaurant)
    }
}