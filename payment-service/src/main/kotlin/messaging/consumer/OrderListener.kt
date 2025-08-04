package com.buoyancy.payment.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.OrderEvent
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.payment.repository.PaymentRepository
import com.buoyancy.payment.service.impl.MockPaymentServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener

class OrderListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var service: MockPaymentServiceImpl
    @Autowired
    private lateinit var repo: PaymentRepository

    @KafkaListener(topics = ["orders"])
    fun receiveOrderRecord(eventRecord: ConsumerRecord<String, OrderEvent>) {

        log.info { "Received order event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val orderId = event.orderId

        if (orderId == null) {
            log.error { "Received order event of type ${event.type} with null id" }
            return
        }

        when (event.type) {
            OrderStatus.CREATED -> service.createPayment(orderId)
            OrderStatus.CANCELLED -> {
                val payments = repo.findByOrderId(orderId)
                payments.forEach { service.cancel(it.id!!) }
            }
            else -> {}
        }
    }
}