package com.buoyancy.payment.service

import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface MockPaymentService {
    fun pay(orderId: UUID, value: Long)
    fun payByPaymentId(paymentId: UUID, value: Long)
    fun cancel(orderId: UUID)
    fun createPayment(payment: Payment): Payment
    fun createPayment(orderId: UUID): Payment
    fun getPayment(orderId: UUID): Payment
    fun getPayments(pageable: Pageable): Page<Payment>
    fun getPaymentByOrderId(orderId: UUID): Payment
    fun updateStatus(paymentId: UUID, status: PaymentStatus, errorReason: String? = null): Payment
    fun checkForExpiredPayment()
}