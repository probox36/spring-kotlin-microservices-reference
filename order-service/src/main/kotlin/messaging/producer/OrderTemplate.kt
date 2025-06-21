package com.buoyancy.order.messaging.producer

import com.buoyancy.common.model.dto.events.OrderEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderTemplate {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var kafka: KafkaTemplate<String, OrderEvent>
    private final val topic = "orders"

    fun produceMessage(event: OrderEvent) {
        log.debug { "Sending ${event.type} event for ${event.orderDto.id} order..." }
        kafka.send(topic, event)
            .thenRun{
                log.debug { "Sent ${event.type} event for ${event.orderDto.id} order" }
            }
    }
}