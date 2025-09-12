package com.buoyancy.notification.messaging.consumer
import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.TopicNames
import com.buoyancy.notification.service.MailService
import com.buoyancy.common.utils.find
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

        fun send(message: String) {
            email.send(
                to = event.userEmail,
                subject = messages.find("email.subjects.order"),
                body = message
            )
        }

        when (event.type) {
            OrderStatus.CREATED -> send(messages.find("notifications.order.created", id))
            OrderStatus.READY -> send(messages.find("notifications.order.ready", id))
            OrderStatus.PREPARING -> send(messages.find("notifications.order.preparing", id))
            OrderStatus.POSTPONED -> send(messages.find("notifications.order.postponed", id))
            OrderStatus.CANCELLED -> send(messages.find("notifications.order.cancelled", id))
            else -> {}
        }
    }
}