package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.events.RestaurantEvent
import com.buoyancy.common.model.enums.RestaurantStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener

class RestaurantsListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @KafkaListener(topics = ["restaurants"])
    fun receiveRestaurantRecord(eventRecord: ConsumerRecord<String, RestaurantEvent>) {
        log.debug { "Received restaurant event ${eventRecord.value()}" }

        val event = eventRecord.value()
        when (event.type) {
            RestaurantStatus.PREPARING -> TODO("Здесь нужно вызвать notification-service и отправить уведомление" +
                    "о начале приготовления заказа и ")
            RestaurantStatus.PREPARED -> TODO("Здесь нужно вызвать notification-service и отправить уведомление " +
                    "о готовности заказа + закрыть заказ")
        }
    }
}