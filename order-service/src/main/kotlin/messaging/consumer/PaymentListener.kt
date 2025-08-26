package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.model.enums.PaymentStatus.*
import com.buoyancy.common.model.enums.TopicNames
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

    @KafkaListener(topics = [TopicNames.PAYMENT], groupId = GroupIds.ORDER_GROUP)
    fun receivePaymentRecord(eventRecord: ConsumerRecord<String, PaymentEvent>) {

        log.info { "Received payment event ${eventRecord.value()}" }
        val event = eventRecord.value()

        when (event.type) {
            SUCCESS -> kafka.sendOrderEvent(OrderEvent(event, OrderStatus.PAID))
            EXPIRED -> {
                service.cancelOrder(event.orderId)
                kafka.sendOrderEvent(OrderEvent(event, OrderStatus.CANCELLED))
            }
            else -> {}
        }
    }
}