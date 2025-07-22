package com.buoyancy.restaurant.messaging.producer

import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class SuborderTemplate {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var kafka: KafkaTemplate<String, SuborderEvent>
    private final val topic = "suborders"

    fun sendSuborderEvent(event: SuborderEvent) {
        log.info { "Sending ${event.type} suborder event for ${event.suborderId} suborder..." }
        kafka.send(topic, event)
            .thenRun {
                log.info { "Sent ${event.type} suborder event for ${event.suborderId} suborder" }
            }
    }
}