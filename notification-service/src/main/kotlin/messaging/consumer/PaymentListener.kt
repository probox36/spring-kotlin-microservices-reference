package com.buoyancy.notification.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.model.enums.TopicNames
import com.buoyancy.common.utils.get
import com.buoyancy.notification.service.NotificationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentListener() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var service: NotificationService

    @KafkaListener(topics = [TopicNames.PAYMENT], groupId = GroupIds.NOTIFICATION_GROUP)
    fun receivePaymentRecord(eventRecord: ConsumerRecord<String, PaymentEvent>) {
        val event = eventRecord.value()
        val orderId = event.orderId

        log.info { "Received payment event ${eventRecord.value()}" }

        when (event.type) {
            PaymentStatus.SUCCESS -> {
                service.notifyUser(
                    event,
                    messages.get("notifications.payment.success", orderId)
                )
                service.notifyRestaurants(orderId)
            }
            PaymentStatus.EXPIRED -> service.notifyUser(
                event,
                messages.get(
                    "notifications.payment.fail",
                    orderId, event.errorReason ?: "not stated"
                )
            )
            else -> {}
        }
    }
}