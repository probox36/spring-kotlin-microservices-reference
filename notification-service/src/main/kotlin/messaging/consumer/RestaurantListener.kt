package com.buoyancy.notification.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.RestaurantEvent
import com.buoyancy.common.model.enums.RestaurantStatus
import com.buoyancy.common.utils.get
import com.buoyancy.notification.service.MailService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RestaurantListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var email: MailService

    @KafkaListener(topics = ["restaurant"])
    fun receiveRestaurantRecord(eventRecord: ConsumerRecord<String, RestaurantEvent>) {
        log.info { "Received restaurant event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val id = event.orderId

        if (id == null) {
            log.error { "Received restaurant event of type ${event.type} with null id" }
            return
        }

        fun send(bodyMessageCode: String) {
            email.send(
                to = event.userEmail,
                subject = messages.get("subjects.order"),
                body = messages.get(bodyMessageCode, id)
            )
        }

        when (event.type) {
            RestaurantStatus.READY -> send("notifications.restaurant.ready")
            RestaurantStatus.POSTPONED -> send("notifications.restaurant.postponed")
            RestaurantStatus.PREPARING -> send("notifications.restaurant.preparing")
        }
    }
}