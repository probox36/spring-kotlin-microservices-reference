package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.order.repository.SuborderRepository
import com.buoyancy.order.service.impl.OrderServiceImpl
import com.buoyancy.order.service.impl.SuborderServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener

class SuborderListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var orderService: OrderServiceImpl
    @Autowired
    private lateinit var suborderService: SuborderServiceImpl
    @Autowired
    private lateinit var suborderRepo: SuborderRepository

    @KafkaListener(topics = ["suborders"])
    fun receiveSuborderRecord(eventRecord: ConsumerRecord<String, SuborderEvent>) {
        log.info { "Received suborder event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val order = suborderService.getSuborder(event.suborderId).order

        if (order.id == null) {
            log.error { "Received suborder event of type ${event.type} with null order id" }
            return
        }

        when (event.type) {
            SuborderStatus.PREPARING -> {
                if (order.status == OrderStatus.CREATED) {
                    orderService.updateStatus(order.id!!, OrderStatus.PREPARING)
                }
            }
            SuborderStatus.POSTPONED -> {
                if (order.status in arrayOf(OrderStatus.PREPARING, OrderStatus.CREATED)) {
                    orderService.updateStatus(order.id!!, OrderStatus.POSTPONED)
                }
            }
            SuborderStatus.READY -> {
                val suborders = suborderRepo.findByOrder(order)
                if (suborders.all { it.status == SuborderStatus.READY }) {
                    log.info { "All suborders of order ${order.id} are ready. Updating parent order status" }
                    orderService.updateStatus(order.id!!, OrderStatus.READY)
                }
            }
            else -> {}
        }
    }
}