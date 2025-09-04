package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.RestaurantDto
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.mapper.RestaurantMapper
import com.buoyancy.common.repository.RestaurantRepository
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.RestaurantService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RestaurantServiceImpl : RestaurantService {

    private val log = KotlinLogging.logger {}
    @Autowired
    private lateinit var self: RestaurantService
    @Autowired
    private lateinit var repo: RestaurantRepository
    @Autowired
    private lateinit var messages: MessageSource
    @Autowired
    private lateinit var mapper: RestaurantMapper

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, key = "#result.id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun createRestaurant(dto: RestaurantDto): RestaurantDto {
        if (dto.id != null && repo.existsById(dto.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.restaurant", dto.id!!)
            throw ConflictException(conflictMessage)
        }
        val restaurant = mapper.toEntity(dto)
        val saved = withHandling { repo.save(restaurant) }
        log.info { "Created restaurant $saved" }
        return mapper.toDto(saved)
    }

    @Cacheable(CacheNames.RESTAURANTS_COLLECTION)
    override fun getRestaurants(pageable: Pageable): Page<RestaurantDto> {
        return repo.findAll(pageable).map { mapper.toDto(it) }
    }

    @Cacheable(CacheNames.RESTAURANTS)
    override fun getRestaurant(id: UUID): RestaurantDto {
        return mapper.toDto(self.getRestaurantEntity(id))
    }

    override fun getRestaurantEntity(id: UUID): Restaurant {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.get("exceptions.not-found.restaurant", id))
        }
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, key = "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateRestaurant(id: UUID, dto: RestaurantDto): RestaurantDto {
        val restaurant = mapper.toEntity(dto)
        restaurant.id = id
        val saved = withHandling { repo.save(restaurant) }
        log.info { "Updated restaurant $id to $dto" }
        return mapper.toDto(saved)
    }

    @Caching(
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true),
        CacheEvict(CacheNames.RESTAURANTS, key = "#id")]
    )
    override fun deleteRestaurant(id: UUID) {
        log.info { "Deleting restaurant $id" }
        repo.deleteById(id)
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, key = "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateName(id: UUID, name: String): RestaurantDto {
        val restaurant = self.getRestaurantEntity(id)
        restaurant.name = name
        log.info { "Updating name of restaurant ${restaurant.id} from ${restaurant.name} to $name" }
        return mapper.toDto(repo.save(restaurant))
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, key = "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateAddress(id: UUID, address: String): RestaurantDto {
        val restaurant = self.getRestaurantEntity(id)
        restaurant.address = address
        log.info { "Updating address of restaurant ${restaurant.id} from ${restaurant.address} to $address" }
        return mapper.toDto(repo.save(restaurant))
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, key = "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updatePhone(id: UUID, phone: String): RestaurantDto {
        val restaurant = self.getRestaurantEntity(id)
        restaurant.phoneNumber = phone
        log.info { "Updating phone number of restaurant ${restaurant.id} from ${restaurant.phoneNumber} to $phone" }
        return mapper.toDto(repo.save(restaurant))
    }

    @Caching(
        put = [CachePut(CacheNames.RESTAURANTS, key = "#id")],
        evict = [CacheEvict(CacheNames.RESTAURANTS_COLLECTION, allEntries = true)]
    )
    override fun updateEmail(id: UUID, email: String): RestaurantDto {
        val restaurant = self.getRestaurantEntity(id)
        restaurant.email = email
        log.info { "Updating email of restaurant ${restaurant.id} from ${restaurant.email} to $email" }
        return mapper.toDto(repo.save(restaurant))
    }
    private fun <T> withHandling(block: () -> T): T {
        return try {
            block()
        } catch (_: EntityNotFoundException) {
            throw NotFoundException(messages.get("exceptions.psql.foreign-key"))
        } catch (_: DataIntegrityViolationException) {
            throw BadRequestException(messages.get("exceptions.psql.integrity"))
        }
    }

}