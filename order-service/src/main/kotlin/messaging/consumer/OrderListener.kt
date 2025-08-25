package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.SuborderStatus.CANCELLED
import com.buoyancy.common.model.enums.TopicNames
import com.buoyancy.common.repository.SuborderRepository
import com.buoyancy.order.service.OrderService
import com.buoyancy.order.service.impl.SuborderServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.transaction.Transactional
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var suborderService: SuborderServiceImpl
    @Autowired
    private lateinit var suborderRepository: SuborderRepository

    @KafkaListener(topics = [TopicNames.ORDER], groupId = GroupIds.ORDER_GROUP)
    @Transactional
    fun receiveOrderRecord(eventRecord: ConsumerRecord<String, OrderEvent>) {
        log.info { "Received order event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val orderId = event.orderId

        when (event.type) {
            OrderStatus.CREATED -> {
                val suborders = suborderService.splitToSuborders(orderId)
                suborders.forEach { suborderService.createSuborder(it) }
            }
            OrderStatus.CANCELLED -> updateChildSuborders(orderId, CANCELLED)
            else -> {}
        }
    }

    private fun updateChildSuborders(orderId: UUID, status: SuborderStatus) {
        val suborders = suborderRepository.findByOrderId(orderId)
        log.info { "Updating status of ${suborders.size} suborders of order $orderId to $status" }
        suborders.forEach {
            it.id?.let { suborderId -> suborderService.updateStatus(suborderId, status) }
                ?: log.error { "Order $orderId has a suborder with null id: $it" }
        }
    }
}