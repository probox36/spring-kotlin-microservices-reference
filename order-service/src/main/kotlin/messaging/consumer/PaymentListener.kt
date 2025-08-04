package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.order.messaging.producer.OrderTemplate
import com.buoyancy.order.service.impl.OrderServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentListener() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var service: OrderServiceImpl
    @Autowired
    private lateinit var kafka: OrderTemplate

    @KafkaListener(topics = ["payments"])
    fun receivePaymentRecord(eventRecord: ConsumerRecord<String, PaymentEvent>) {

        log.info { "Received payment event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val id = event.orderId

        if (id == null) {
            log.error { "Received order event of type ${event.type} with null id" }
            return
        }

        when (event.type) {
            PaymentStatus.SUCCESS -> kafka.sendOrderEvent(OrderEvent(event, OrderStatus.PAID))
            PaymentStatus.EXPIRED -> {
                service.cancelOrder(event.orderId!!)
                kafka.sendOrderEvent(OrderEvent(event, OrderStatus.CANCELLED))
            }
            else -> {}
        }
    }
}