package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.SuborderStatus.*
import com.buoyancy.common.model.mapper.SuborderMapper
import com.buoyancy.common.repository.SuborderRepository
import com.buoyancy.common.utils.find
import com.buoyancy.restaurant.messaging.producer.SuborderTemplate
import com.buoyancy.restaurant.service.SuborderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    override fun markSuborderAsPreparing(id: UUID): SuborderDto {
        val status = getSuborder(id).status
        if (status in arrayOf(CREATED, POSTPONED)) {
            return updateStatus(id, PREPARING)
        } else {
            throw BadRequestException(
                messages.find("exceptions.bad-request.order.status-change", status, PREPARING)
            )
        }
    }

    override fun markSuborderAsReady(id: UUID): SuborderDto {
        val status = getSuborder(id).status
        if (status == PREPARING) {
            return updateStatus(id, READY)
        } else {
            throw BadRequestException(
                messages.find("exceptions.bad-request.order.status-change", status, READY)
            )
        }
    }

    override fun postponeSuborder(id: UUID): SuborderDto {
        val status = getSuborder(id).status
        if (status in arrayOf(CREATED, PREPARING)) {
            return updateStatus(id, POSTPONED)
        } else {
            throw BadRequestException(
                messages.find("exceptions.bad-request.order.status-change", status, POSTPONED)
            )
        }
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
        put = [CachePut(CacheNames.SUBORDERS, key = "#id")],
        evict = [CacheEvict(CacheNames.SUBORDER_COLLECTION, allEntries = true)]
    )
    @Transactional
    private fun updateStatus(id: UUID, status: SuborderStatus): SuborderDto {
        val suborder = self.getSuborderEntity(id)
        suborder.status = status
        kafka.sendSuborderEvent(SuborderEvent(status, suborder.id!!))
        log.info { "Changed status of suborder ${suborder.id} to $status" }
        return mapper.toDto(repo.save(suborder))
    }
}