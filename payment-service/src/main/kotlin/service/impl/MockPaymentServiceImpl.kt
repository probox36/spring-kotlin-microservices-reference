package com.buoyancy.payment.service.impl

import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.repository.OrderRepository
import com.buoyancy.common.repository.PaymentRepository
import com.buoyancy.common.utils.get
import com.buoyancy.payment.messaging.producer.PaymentTemplate
import com.buoyancy.payment.service.MockPaymentService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class MockPaymentServiceImpl: MockPaymentService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var self: MockPaymentServiceImpl
    @Autowired
    private lateinit var orderRepo: OrderRepository
    @Autowired
    private lateinit var paymentRepo: PaymentRepository
    @Autowired
    private lateinit var kafka: PaymentTemplate
    @Autowired
    private lateinit var messageSource: MessageSource

    @Transactional
    override fun payByPaymentId(paymentId: UUID, value: Long) {
        val payment = getPayment(paymentId)
        payment.valuePaid += value
        paymentRepo.save(payment)
        val left = (payment.value - payment.valuePaid).coerceIn(0, payment.value)
        log.info { "Paid $value to payment $paymentId. $left left; $payment" }
        if (payment.valuePaid >= payment.value) {
            log.info { "Order ${payment.id} was fully paid" }
            self.updateStatus(paymentId, PaymentStatus.SUCCESS)
        }
    }

    override fun pay(orderId: UUID, value: Long) {
        payByPaymentId(getPaymentByOrderId(orderId).id!!, value)
    }

    override fun cancel(paymentId: UUID) {
        self.updateStatus(paymentId, PaymentStatus.EXPIRED)
    }

    override fun createPayment(payment: Payment): Payment {
        log.info { "Creating payment $payment" }
        val created = paymentRepo.save(payment)
        log.info { "Payment ${ created.id } created" }
        return created
    }

    @Transactional
    override fun createPayment(orderId: UUID): Payment {
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

    @Transactional
    override fun updateStatus(paymentId: UUID, status: PaymentStatus, errorReason: String?): Payment {
        val payment = getPayment(paymentId)
        var updated = payment
        if (payment.status != status) {

            val order = payment.order
            val orderId = requireNotNull(order.id) { "Payment ${payment.id}'s corresponding order has null id" }
            val userId = requireNotNull(order.user.id) { "Payment ${payment.id}'s corresponding user has null id" }

            log.info { "Updating status of payment $paymentId from ${payment.status} to $status" }
            payment.status = status
            updated = paymentRepo.save(payment)
            afterCommit {
                kafka.sendPaymentEvent(PaymentEvent(
                    type = status,
                    orderId = orderId,
                    userId = userId,
                    userEmail = order.user.email,
                    errorReason = errorReason)
                )
                log.info { "Updated status of payment $paymentId to $status" }
            }
        }
        return updated
    }

    override fun getPaymentByOrderId(orderId: UUID): Payment {
        return paymentRepo.findFirstByOrderIdAndStatus(orderId)
            ?: throw NotFoundException("Order with id $orderId not found")
    }

    override fun getPayments(pageable: Pageable): Page<Payment> {
        return paymentRepo.findAll(pageable)
    }

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.MINUTES)
    override fun checkForExpiredPayment() {
        log.info { "Checking for expired payments" }
        val reason = messageSource.get("notifications.payment.error.expired")
        val expired = paymentRepo.findByStatusAndTimeBefore()
        expired.forEach { self.updateStatus(it.id!!, PaymentStatus.EXPIRED, reason) }
        log.info { "Found ${expired.size} expired payments. Marked them as EXPIRED" }
    }

    private fun calculateValue(order: Order): Long {
        return order.items.sumOf { it.price }
    }

    private fun afterCommit(action: () -> Unit) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() { action() }
        })
    }
}