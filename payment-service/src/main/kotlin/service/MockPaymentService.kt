package com.buoyancy.payment.service

import com.buoyancy.common.model.dto.PaymentDto
import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface MockPaymentService {
    fun pay(orderId: UUID, value: Long): PaymentDto
    fun payByPaymentId(paymentId: UUID, value: Long): PaymentDto
    fun cancel(orderId: UUID): PaymentDto
    fun delete(paymentId: UUID)
    fun createPayment(payment: PaymentDto): PaymentDto
    fun createPayment(orderId: UUID): PaymentDto
    fun getPayment(orderId: UUID): PaymentDto
    fun getPaymentEntity(paymentId: UUID): Payment
    fun getPayments(pageable: Pageable): Page<PaymentDto>
    fun getPaymentByOrderIdAndStatus(orderId: UUID, status: PaymentStatus): PaymentDto
    fun getPaymentByOrderId(orderId: UUID): PaymentDto
    fun updatePayment(id: UUID, dto: PaymentDto): PaymentDto
    fun updateStatus(paymentId: UUID, status: PaymentStatus, errorReason: String? = null): PaymentDto
    fun checkForExpiredPayment()
}