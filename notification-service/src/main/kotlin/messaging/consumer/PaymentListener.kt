package com.buoyancy.notification.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.utils.get
import com.buoyancy.notification.service.MailService
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
    private lateinit var email: MailService

    @KafkaListener(topics = ["payments"])
    fun receivePaymentRecord(eventRecord: ConsumerRecord<String, PaymentEvent>) {
        val event = eventRecord.value()
        val id = event.orderId

        if (id == null) {
            log.error { "Received payment event of type ${event.type} with null id" }
            return
        }

        log.info { "Received payment event ${eventRecord.value()}" }

        fun send(messageBodyCode: String) {
            email.send(
                to = event.userEmail,
                subject = messages.get("subjects.payment"),
                body = messages.get(messageBodyCode, id)
            )
        }

        when (event.type) {
            PaymentStatus.SUCCESS -> send("notifications.payment.success")
            PaymentStatus.EXPIRED -> send(
                messages.get(
                    "notifications.payment.fail",
                    id, event.errorReason ?: "not stated"
                )
            )
            else -> {}
        }
    }
}