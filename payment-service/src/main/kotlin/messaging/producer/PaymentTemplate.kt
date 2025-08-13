package com.buoyancy.payment.messaging.producer

import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.enums.TopicNames
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentTemplate {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var kafka: KafkaTemplate<String, PaymentEvent>
    private final val topic = TopicNames.PAYMENT

    fun sendPaymentEvent(event: PaymentEvent) {
        log.info { "Sending ${event.type} payment event for ${event.orderId} order..." }
        kafka.send(topic, event)
            .thenRun {
                log.info { "Sent ${event.type} payment event for ${event.orderId} order" }
            }
    }
}