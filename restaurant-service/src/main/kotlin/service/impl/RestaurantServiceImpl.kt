package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.repository.RestaurantRepository
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.RestaurantService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RestaurantServiceImpl : RestaurantService {

    private val log = KotlinLogging.logger {}
    @Autowired
    private lateinit var repo: RestaurantRepository
    @Autowired
    private lateinit var messages: MessageSource

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, "#restaurant.id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun createRestaurant(restaurant: Restaurant): Restaurant {
        if (restaurant.id != null && repo.existsById(restaurant.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.restaurant", restaurant.id!!)
            throw ConflictException(conflictMessage)
        }
        log.info { "Creating restaurant $restaurant" }
        return repo.save(restaurant)
    }

    @Cacheable(CacheNames.RESTAURANTS_COLLECTION)
    override fun getRestaurants(pageable: Pageable): Page<Restaurant> {
        return repo.findAll(pageable)
    }

    @Cacheable(CacheNames.RESTAURANTS)
    override fun getRestaurant(id: UUID): Restaurant {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.get("exceptions.not-found.restaurant", id))
        }
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateRestaurant(id: UUID, restaurant: Restaurant): Restaurant {
        restaurant.id = id
        log.info { "Updating restaurant $restaurant" }
        return repo.save(restaurant)
    }

    @Caching(
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true),
        CacheEvict(CacheNames.RESTAURANTS, "#id")]
    )
    override fun deleteRestaurant(id: UUID) {
        log.info { "Deleting restaurant $id" }
        repo.deleteById(id)
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateName(id: UUID, name: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.name = name
        log.info { "Updating name of restaurant ${restaurant.id} from ${restaurant.name} to $name" }
        return repo.save(restaurant)
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateAddress(id: UUID, address: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.address = address
        log.info { "Updating address of restaurant ${restaurant.id} from ${restaurant.address} to $address" }
        return repo.save(restaurant)
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updatePhone(id: UUID, phone: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.phoneNumber = phone
        log.info { "Updating phone number of restaurant ${restaurant.id} from ${restaurant.phoneNumber} to $phone" }
        return repo.save(restaurant)
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateEmail(id: UUID, email: String): Restaurant {
        val restaurant = getRestaurant(id)
        restaurant.email = email
        log.info { "Updating email of restaurant ${restaurant.id} from ${restaurant.email} to $email" }
        return repo.save(restaurant)
    }
}