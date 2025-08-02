package com.buoyancy.notification.messaging.consumer
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.notification.service.MailService
import com.buoyancy.common.utils.get
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderListener() {

    private val log = KotlinLogging.logger {}
    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var email: MailService

    @KafkaListener(topics = ["orders"])
    fun receiveOrderRecord(eventRecord: ConsumerRecord<String, OrderEvent>) {
        log.info { "Received order event ${eventRecord.value()}" }
        val event = eventRecord.value()

        fun send(messageBodyCode: String) {
            email.send(
                to = event.userEmail,
                subject = messages.get("subjects.order"),
                body = messages.get(messageBodyCode, event.orderId)
            )
        }

        when (event.type) {
            OrderStatus.CREATED -> send("notifications.order.created")
            OrderStatus.CLOSED -> send("notifications.order.closed")
            OrderStatus.CANCELLED -> send("notifications.order.cancelled")
            else -> {}
        }
    }
}