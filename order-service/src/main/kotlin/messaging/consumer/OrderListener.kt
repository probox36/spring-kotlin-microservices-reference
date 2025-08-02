package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.exceptions.InternalErrorException
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.SuborderStatus.*
import com.buoyancy.common.utils.get
import com.buoyancy.order.repository.SuborderRepository
import com.buoyancy.order.service.OrderService
import com.buoyancy.order.service.impl.SuborderServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.kafka.annotation.KafkaListener
import java.util.UUID

class OrderListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var suborderService: SuborderServiceImpl
    @Autowired
    private lateinit var suborderRepository: SuborderRepository
    @Autowired
    private lateinit var orderService: OrderService

    @KafkaListener(topics = ["orders"])
    fun receiveOrderRecord(eventRecord: ConsumerRecord<String, OrderEvent>) {
        log.info { "Received order event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val id = event.orderId

        if (id == null) {
            log.error { "Received order event of type ${event.type} with null id" }
            return
        }

        when (event.type) {
            OrderStatus.CREATED -> {
                val order = orderService.getOrder(id)
                val suborders = suborderService.splitToSuborders(order)
                suborders.forEach { suborderService.createSuborder(it) }
            }
            OrderStatus.CANCELLED -> updateChildSuborders(id, CANCELLED)
            else -> {}
        }
    }

    private fun updateChildSuborders(orderId: UUID, status: SuborderStatus) {
        val suborders = suborderRepository.findByOrderId(orderId)
        log.info { "Updating status of ${suborders.size} suborders of order $orderId to $status" }
        suborders.forEach { suborder ->
            if (suborder.id != null) {
                suborderService.updateStatus(suborder.id!!, status)
            } else {
                log.error { "Order $orderId has a suborder with null id: $suborder" }
            }
        }
    }
}