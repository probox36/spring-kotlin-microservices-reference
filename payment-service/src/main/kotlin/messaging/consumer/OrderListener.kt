package com.buoyancy.payment.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.TopicNames
import com.buoyancy.common.repository.PaymentRepository
import com.buoyancy.payment.service.impl.MockPaymentServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var service: MockPaymentServiceImpl
    @Autowired
    private lateinit var repo: PaymentRepository

    @KafkaListener(topics = [TopicNames.ORDER], groupId = GroupIds.PAYMENT_GROUP)
    fun receiveOrderRecord(eventRecord: ConsumerRecord<String, OrderEvent>) {

        log.info { "Received order event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val orderId = event.orderId

        when (event.type) {
            OrderStatus.CREATED -> service.createPayment(orderId)
            OrderStatus.CANCELLED -> {
                val payment = service.getPaymentByOrderId(orderId)
                service.cancel(payment.id!!)
            }
            else -> {}
        }
    }
}