package com.buoyancy.order.messaging
//
//import io.github.oshai.kotlinlogging.KotlinLogging
//// import model.dto.events.PaymentEvent
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.springframework.kafka.annotation.KafkaListener
//
//class PaymentsListener {
//
//    private val log = KotlinLogging.logger {}
//
//    @KafkaListener(topics = ["payments"])
//    fun recievePaymentRecord(payment: ConsumerRecord<String, PaymentEvent>) {
//        log.debug { "Received payment event ${payment.value()}" }

//        val objectMapper = ObjectMapper()
//        objectMapper.registerModule(JavaTimeModule())
//
//        val value = paymentStr.value()
//        try {
//            val orderDto: OrderDto = objectMapper.readValue<OrderDto>(value, OrderDto::class.java)
//            val order: Order = converter.toOrder(orderDto)
//            repository.save(order)
//        } catch (e: JsonProcessingException) {
//            log.error(
//                """
//                Had a hard time deserializing following json: {}
//                Stack trace: {}
//                """.trimIndent(), value, e.stackTrace
//            )
//        } catch (e: DataAccessException) {
//            log.error("You messed things up with the db. Reason: {}", e.stackTrace as Any)
//        }
//    }
//}