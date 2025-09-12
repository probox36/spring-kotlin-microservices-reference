package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.SuborderStatus.CREATED
import com.buoyancy.common.model.mapper.SuborderMapper
import com.buoyancy.common.repository.SuborderRepository
import com.buoyancy.common.utils.find
import com.buoyancy.order.messaging.producer.SuborderTemplate
import com.buoyancy.order.service.OrderService
import com.buoyancy.order.service.SuborderService
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
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization
import java.util.*

@Service
class SuborderServiceImpl : SuborderService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var self: SuborderServiceImpl
    @Autowired
    private lateinit var repo: SuborderRepository
    @Autowired
    private lateinit var kafka: SuborderTemplate
    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var mapper: SuborderMapper
    @Autowired
    private lateinit var orderService: OrderService

    @Transactional
    @Caching(
        put = [CachePut(CacheNames.SUBORDERS, key = "#result.id")],
        evict = [CacheEvict(CacheNames.SUBORDER_COLLECTION, allEntries = true)]
    )
    override fun createSuborder(suborderDto: SuborderDto): SuborderDto {
        if (suborderDto.id != null && repo.existsById(suborderDto.id!!)) {
            throw ConflictException(messages.find("exceptions.conflict.suborder", suborderDto.id!!))
        }

        val suborder = mapper.toEntity(suborderDto).apply { status = CREATED }
        val saved = withHandling { repo.save(suborder) }
        log.info { "Created suborder ${saved.id} for order ${suborderDto.orderId}" }

        afterCommit {
            kafka.sendSuborderEvent(SuborderEvent(CREATED, saved.id!!))
            log.info { "Suborder for order ${saved.order.id} created and message sent: $saved" }
        }
        return mapper.toDto(saved)
    }

    @Cacheable(CacheNames.SUBORDER_COLLECTION, key = "{#restaurantId, #pageable}")
    override fun getSubordersByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<SuborderDto> {
        return repo.findByRestaurantId(restaurantId, pageable).map { mapper.toDto(it) }
    }

    @Cacheable(CacheNames.SUBORDER_COLLECTION, key = "{#restaurantId, #pageable, #status}")
    override fun getSubordersByRestaurantIdAndStatus(restaurantId: UUID, status: SuborderStatus, pageable: Pageable): Page<SuborderDto> {
        return repo.findByRestaurantIdAndStatus(restaurantId, status, pageable).map { mapper.toDto(it) }
    }

    @Cacheable(CacheNames.SUBORDER_COLLECTION)
    override fun getSuborders(pageable: Pageable): Page<SuborderDto> {
        return repo.findAll(pageable).map { mapper.toDto(it) }
    }

    @Caching(
        put = [CachePut(CacheNames.SUBORDERS, key = "#id")],
        evict = [CacheEvict(CacheNames.SUBORDER_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun updateStatus(id: UUID, status: SuborderStatus): SuborderDto {
        val suborder = self.getSuborderEntity(id)
        suborder.status = status
        repo.save(suborder)
        afterCommit {
            kafka.sendSuborderEvent(SuborderEvent(status, id))
            log.info { "Changed status of suborder $id to $status" }
        }
        return mapper.toDto(suborder)
    }

    @Cacheable(CacheNames.SUBORDERS)
    override fun getSuborder(id: UUID): SuborderDto {
        return mapper.toDto(self.getSuborderEntity(id))
    }

    override fun getSuborderEntity(id: UUID): Suborder {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.find("exceptions.not-found.suborder", id))
        }
    }

    @Caching(
        evict = [CacheEvict(CacheNames.SUBORDER_COLLECTION, allEntries = true),
            CacheEvict(CacheNames.SUBORDERS, key = "#id")]
    )
    override fun deleteSuborder(id: UUID) {
        log.info { "Deleting suborder $id" }
        repo.deleteById(id)
    }

    @Caching(
        put = [CachePut(CacheNames.ORDERS, key = "#id")],
        evict = [CacheEvict(CacheNames.SUBORDER_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun updateSuborder(id: UUID, update: SuborderDto): SuborderDto {
        val order = self.getSuborderEntity(id)
        val updated = mapper.toEntity(update).apply { this.id = id }
        log.info { "Updating suborder ${order.id} to $order" }
        return withHandling {
            mapper.toDto(repo.save(updated))
        }
    }

    override fun splitToSuborders(orderId: UUID): List<SuborderDto> {
        val order = orderService.getOrderEntity(orderId)
        val subordersMap = order.items.groupBy { it.restaurant.id }
        val suborders = subordersMap.map { (restaurantId, items) ->
            val itemIds = items.mapNotNull { it.id }
            SuborderDto(
                id = null,
                orderId = order.id!!,
                restaurantId = restaurantId!!,
                items = itemIds,
                status = CREATED
            )
        }

        log.info { "Split order ${order.id} to ${suborders.size} suborders" }
        return suborders
    }

    private fun afterCommit(action: () -> Unit) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() { action() }
        })
    }

    private fun <T> withHandling(block: () -> T): T {
        return try {
            block()
        } catch (_: EntityNotFoundException) {
            throw NotFoundException(messages.find("exceptions.psql.foreign-key"))
        } catch (_: DataIntegrityViolationException) {
            throw BadRequestException(messages.find("exceptions.psql.integrity"))
        }
    }
}