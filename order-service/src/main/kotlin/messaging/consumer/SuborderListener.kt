package com.buoyancy.order.messaging.consumer

import com.buoyancy.common.model.dto.messaging.events.SuborderEvent
import com.buoyancy.common.model.enums.GroupIds
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.enums.TopicNames
import com.buoyancy.common.repository.SuborderRepository
import com.buoyancy.order.service.impl.OrderServiceImpl
import com.buoyancy.order.service.impl.SuborderServiceImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SuborderListener {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var orderService: OrderServiceImpl
    @Autowired
    private lateinit var suborderService: SuborderServiceImpl
    @Autowired
    private lateinit var suborderRepo: SuborderRepository

    @KafkaListener(topics = [TopicNames.SUBORDER], groupId = GroupIds.ORDER_GROUP)
    fun receiveSuborderRecord(eventRecord: ConsumerRecord<String, SuborderEvent>) {
        log.info { "Received suborder event ${eventRecord.value()}" }
        val event = eventRecord.value()
        val suborder = suborderService.getSuborder(event.suborderId)
        val order = orderService.getOrder(suborder.orderId)

        requireNotNull(order.id) { "Received suborder event of type ${event.type} with null suborder id" }

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
                val suborders = suborderRepo.findByOrderId(order.id!!)
                if (suborders.all { it.status == SuborderStatus.READY }) {
                    log.info { "All suborders of order ${order.id} are ready. Updating parent order status" }
                    orderService.updateStatus(order.id!!, OrderStatus.READY)
                }
            }
            else -> {}
        }
    }
}