package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.events.PaymentEvent
import com.buoyancy.common.model.enums.PaymentStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener

class PaymentsListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @KafkaListener(topics = ["payments"])
    fun receivePaymentRecord(eventRecord: ConsumerRecord<String, PaymentEvent>) {
        log.debug { "Received payment event ${eventRecord.value()}" }

        val event = eventRecord.value()
        when (event.type) {
            PaymentStatus.PENDING -> TODO()
            PaymentStatus.SUCCESS -> TODO("Здесь нужно вызвать RestaurantService и начать готовить заказ")
            PaymentStatus.FAILED -> TODO("Здесь я хз что нужно сделать")
        }
    }
}