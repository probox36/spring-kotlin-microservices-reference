package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.SuborderStatus.CREATED
import com.buoyancy.common.repository.SuborderRepository
import com.buoyancy.common.utils.get
import com.buoyancy.order.messaging.producer.SuborderTemplate
import com.buoyancy.order.service.SuborderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
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

    @Transactional
    override fun createSuborder(suborder: Suborder): Suborder {
        if (suborder.id != null && repo.existsById(suborder.id!!)) {
            throw ConflictException(messages.get("exceptions.conflict.suborder", suborder.id!!))
        }
        log.info { "Creating suborder for order ${suborder.order.id}" }
        suborder.status = CREATED
        val savedSuborder = repo.save(suborder)
        afterCommit {
            kafka.sendSuborderEvent(SuborderEvent(CREATED, suborder.id!!))
            log.info { "Suborder for order ${savedSuborder.order.id} created and message sent: $savedSuborder" }
        }
        return savedSuborder
    }

    @Transactional
    override fun updateStatus(id: UUID, status: SuborderStatus) {
        val suborder = getSuborder(id)
        suborder.status = status
        repo.save(suborder)
        afterCommit {
            kafka.sendSuborderEvent(SuborderEvent(status, id))
            log.debug { "Changed status of suborder $id to $status" }
        }
    }

    override fun getSuborder(id: UUID): Suborder {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.get("exceptions.not-found.suborder", id))
        }
    }

    @Transactional
    override fun splitToSuborders(order: Order): List<Suborder> {
        val subordersMap = hashMapOf<Restaurant, Suborder>()

        for (item in order.items) {
            if (item.restaurant !in subordersMap.keys) {
                subordersMap[item.restaurant] = Suborder(
                    id = null,
                    order = order,
                    restaurant = item.restaurant,
                    items = mutableListOf(),
                    status = CREATED
                )
            }
            subordersMap[item.restaurant]!!.items.add(item)
        }
        val suborders = subordersMap.values.toList()
        log.info { "Split order ${order.id} to ${suborders.size} suborders" }
        return suborders
    }

    private fun afterCommit(action: () -> Unit) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() { action() }
        })
    }
}