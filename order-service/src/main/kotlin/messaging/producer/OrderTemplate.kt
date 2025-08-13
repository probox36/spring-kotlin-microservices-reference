package com.buoyancy.order.messaging.producer

import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.TopicNames
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderTemplate {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var kafka: KafkaTemplate<String, OrderEvent>
    private final val topic = TopicNames.ORDER

    fun sendOrderEvent(event: OrderEvent) {
        log.info { "Sending ${event.type} order event for ${event.orderId} order..." }
        kafka.send(topic, event)
            .thenRun {
                log.info { "Sent ${event.type} order event for ${event.orderId} order" }
            }
    }
}