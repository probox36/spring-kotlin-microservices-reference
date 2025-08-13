package com.buoyancy.notification.messaging.consumer
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.TopicNames
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

    @KafkaListener(topics = [TopicNames.ORDER], groupId = GroupIds.NOTIFICATION_GROUP)
    fun receiveOrderRecord(eventRecord: ConsumerRecord<String, OrderEvent>) {
        val event = eventRecord.value()
        val id = event.orderId

        log.info { "Received order event $event" }

        fun send(messageBodyCode: String) {
            email.send(
                to = event.userEmail,
                subject = messages.get("email.subjects.order"),
                body = messages.get(messageBodyCode, id)
            )
        }

        when (event.type) {
            OrderStatus.CREATED -> send("notifications.order.created")
            OrderStatus.READY -> send("notifications.order.ready")
            OrderStatus.PREPARING -> send("notifications.order.preparing")
            OrderStatus.POSTPONED -> send("notifications.order.postponed")
            OrderStatus.CANCELLED -> send("notifications.order.cancelled")
            else -> {}
        }
    }
}