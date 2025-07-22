package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.RestaurantEvent
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.RestaurantStatus
import com.buoyancy.order.service.impl.OrderServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener

class RestaurantListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var orderService: OrderServiceImpl

    @KafkaListener(topics = ["restaurants"])
    fun receiveRestaurantRecord(eventRecord: ConsumerRecord<String, RestaurantEvent>) {
        log.info { "Received restaurant event ${eventRecord.value()}" }

        val event = eventRecord.value()
        when (event.type) {
            RestaurantStatus.PREPARING -> {
                orderService.updateStatus(event.orderId, OrderStatus.PREPARING)
            }
            RestaurantStatus.READY -> {
                orderService.updateStatus(event.orderId, OrderStatus.READY)
            }
            RestaurantStatus.POSTPONED -> {
                orderService.updateStatus(event.orderId, OrderStatus.POSTPONED)
            }
        }
    }
}