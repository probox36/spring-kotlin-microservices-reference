package com.buoyancy.payment.service.impl

import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.payment.messaging.producer.PaymentTemplate
import com.buoyancy.payment.repository.OrderRepository
import com.buoyancy.payment.repository.PaymentRepository
import com.buoyancy.payment.service.MockPaymentService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import java.util.*

class MockPaymentServiceImpl: MockPaymentService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var orderRepo: OrderRepository
    @Autowired
    private lateinit var paymentRepo: PaymentRepository
    @Autowired
    private lateinit var kafka: PaymentTemplate

    override fun pay(paymentId: UUID, value: Long) {
        log.info { "Paying $value to order $paymentId" }
        val order = getPayment(paymentId)
        order.valuePaid.plus(value)
        if (order.valuePaid >= order.value) {
            log.info { "Order ${order.id} was fully paid" }
            updateStatus(paymentId, PaymentStatus.SUCCESS)
        }
    }

    override fun cancel(paymentId: UUID) {
        updateStatus(paymentId, PaymentStatus.EXPIRED)
    }

    override fun createPayment(payment: Payment): Payment {
        log.info { "Creating payment $payment" }
        return paymentRepo.save(payment)
    }

    override fun createPayment(orderId: UUID): Payment {
        log.info { "Creating payment for order $orderId" }
        val order = orderRepo.findById(orderId).orElseThrow {
            NotFoundException("Order with id $orderId not found")
        }
        val value = calculateValue(order)
        return createPayment(Payment(
            id = null,
            order = order,
            value = value
        ))
    }

    override fun getPayment(paymentId: UUID): Payment {
        return paymentRepo.findById(paymentId).orElseThrow {
            NotFoundException("Payment with id $paymentId not found")
        }
    }

    override fun updateStatus(paymentId: UUID, status: PaymentStatus): Payment {
        val payment = getPayment(paymentId)
        var updated = payment
        if (payment.status != status) {
            log.info { "Updating status of payment $paymentId from ${payment.status} to $status" }
            payment.status = status
            val order = payment.order
            updated = paymentRepo.save(payment)
            kafka.sendPaymentEvent(PaymentEvent(
                type = status,
                orderId = order.id,
                userId = order.user.id,
                userEmail = order.user.email)
            )
        }
        return updated
    }

    override fun getPaymentByOrderId(orderId: UUID): Payment {
        return paymentRepo.findByOrderId(orderId).first()
    }

    override fun getPayments(pageable: Pageable): Page<Payment> {
        return paymentRepo.findAll(pageable)
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    override fun checkForExpiredPayment() {
        log.info { "Checking for expired payments" }
        val expired = paymentRepo.findByStatusAndCreatedTimeBefore()
        expired.forEach { updateStatus(it.id!!, PaymentStatus.EXPIRED) }
        log.info { "Found ${expired.size} expired payments. Marked them as EXPIRED" }
    }

    private fun calculateValue(order: Order): Long {
        return order.items.sumOf { it.price }
    }
}