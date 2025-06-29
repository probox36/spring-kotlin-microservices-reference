package com.buoyancy.notification.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.notification.service.MailService
import com.buoyancy.notification.util.get
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentsListener() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var email: MailService

    @KafkaListener(topics = ["payments"])
    fun receivePaymentRecord(eventRecord: ConsumerRecord<String, PaymentEvent>) {
        log.info { "Received payment event ${eventRecord.value()}" }

        val event = eventRecord.value()
        when (event.type) {
            PaymentStatus.SUCCESS -> email.send(
                to = event.userEmail,
                subject = messages.get("subjects.payment"),
                body = messages.get("notifications.payment.success", arrayOf(event.orderId))
            )
            PaymentStatus.FAILED -> email.send(
                to = event.userEmail,
                subject = messages.get("subjects.payment"),
                body = messages.get(
                    code = "notifications.payment.fail",
                    arg = event.errorReason?.let {
                        arrayOf(event.orderId, it)
                    } ?: arrayOf(event.orderId)
                )
            )
            PaymentStatus.PENDING -> {}
        }
    }
}