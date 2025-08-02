package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.repository.RestaurantRepository
import com.buoyancy.restaurant.service.RestaurantService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class RestaurantServiceImpl : RestaurantService {

    private val log = KotlinLogging.logger {}
    @Autowired
    private lateinit var repo: RestaurantRepository
    @Autowired
    private lateinit var messages: MessageSource

    override fun createRestaurant(restaurant: Restaurant): Restaurant {
        if (restaurant.id != null && repo.existsById(restaurant.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.restaurant", restaurant.id!!)
            throw ConflictException(conflictMessage)
        }
        log.info { "Creating restaurant $restaurant" }
        return repo.save(restaurant)
    }

    override fun getRestaurants(pageable: Pageable): Page<Restaurant> {
        return repo.findAll(pageable)
    }

    override fun getRestaurant(id: UUID): Restaurant {
        return repo.findById(id).orElseThrow {
            NotFoundException("Restaurant with id $id not found")
        }
    }

    override fun updateRestaurant(id: UUID, restaurant: Restaurant): Restaurant {
        restaurant.id = id
        log.info { "Updating restaurant $restaurant" }
        return repo.save(restaurant)
    }

    override fun deleteRestaurant(id: UUID) {
        log.info { "Deleting restaurant $id" }
        repo.deleteById(id)
    }

    override fun updateName(id: UUID, name: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.name = name
        log.info { "Updating name of restaurant ${restaurant.id} from ${restaurant.name} to $name" }
        return repo.save(restaurant)
    }

    override fun updateAddress(id: UUID, address: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.address = address
        log.info { "Updating address of restaurant ${restaurant.id} from ${restaurant.address} to $address" }
        return repo.save(restaurant)
    }

    override fun updatePhone(id: UUID, phone: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.phoneNumber = phone
        log.info { "Updating phone number of restaurant ${restaurant.id} from ${restaurant.phoneNumber} to $phone" }
        return repo.save(restaurant)
    }

    override fun updateEmail(id: UUID, email: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.email = email
        log.info { "Updating email of restaurant ${restaurant.id} from ${restaurant.email} to $email" }
        return repo.save(restaurant)
    }
}