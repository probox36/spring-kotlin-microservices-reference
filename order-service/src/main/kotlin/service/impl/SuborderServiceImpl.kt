package com.buoyancy.order.service.impl

import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.SuborderStatus.*
import com.buoyancy.order.messaging.producer.SuborderTemplate
import com.buoyancy.order.repository.SuborderRepository
import com.buoyancy.order.service.SuborderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class SuborderServiceImpl : SuborderService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var repo: SuborderRepository
    @Autowired
    private lateinit var kafka: SuborderTemplate


    override fun createSuborder(suborder: Suborder): Suborder {
        log.info { "Creating suborder" }
        suborder.status = CREATED
        val saved = repo.save(suborder)
        kafka.sendSuborderEvent(SuborderEvent(CREATED, saved.id))
        log.info { "Suborder ${saved.id} created" }
        return saved
    }

    override fun getStatus(id: UUID): SuborderStatus {
        return getSuborder(id).status
    }

    override fun markSuborderAsPreparing(id: UUID) {
        updateStatus(id, PREPARING)
    }

    override fun markSuborderAsPostponed(id: UUID) {
        updateStatus(id, POSTPONED)
    }

    override fun markSuborderAsReady(id: UUID) {
        updateStatus(id, READY)
    }

    override fun updateStatus(id: UUID, status: SuborderStatus) {
        val suborder = getSuborder(id)
        suborder.status = status
        repo.save(suborder)
        kafka.sendSuborderEvent(SuborderEvent(status, id))
        log.debug { "Changed status of suborder $id to $status" }
    }

    override fun getSuborder(id: UUID): Suborder {
        return repo.findById(id).orElseThrow {
            NotFoundException("Suborder with id $id not found")
        }
    }

    override fun splitToSuborders(order: Order): List<Suborder> {
        val suborders = hashMapOf<Restaurant, Suborder>()

        for (item in order.items) {
            if (item.restaurant !in suborders.keys) {
                suborders[item.restaurant] = Suborder(
                    id = UUID.randomUUID(),
                    order = order,
                    restaurant = item.restaurant,
                    items = mutableListOf(),
                    status = CREATED
                )
            }
            suborders[item.restaurant]!!.items.add(item)
        }
        return suborders.values.toList()
    }
}