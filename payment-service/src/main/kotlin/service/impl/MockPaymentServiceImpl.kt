package com.buoyancy.payment.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.PaymentDto
import com.buoyancy.common.model.dto.messaging.events.PaymentEvent
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.model.mapper.PaymentMapper
import com.buoyancy.common.repository.OrderRepository
import com.buoyancy.common.repository.PaymentRepository
import com.buoyancy.common.utils.find
import com.buoyancy.payment.messaging.producer.PaymentTemplate
import com.buoyancy.payment.service.MockPaymentService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
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
    private lateinit var messages: MessageSource
    @Autowired
    private lateinit var mapper: PaymentMapper

    @Caching(
        put = [CachePut(CacheNames.PAYMENTS, key = "#paymentId")],
        evict = [CacheEvict(CacheNames.PAYMENT_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun payByPaymentId(paymentId: UUID, value: Long): PaymentDto {
        if (value <= 0) { throw BadRequestException(
            messages.find("exceptions.bad-request.payment.value-negative"))
        }
        val payment = self.getPaymentEntity(paymentId)
        payment.valuePaid += value
        paymentRepo.save(payment)
        val left = (payment.value - payment.valuePaid).coerceIn(0, payment.value)
        log.info { "Paid $value to payment $paymentId. $left left; $payment" }
        if (payment.valuePaid >= payment.value) {
            log.info { "Order ${payment.id} was fully paid" }
            self.updateStatus(paymentId, PaymentStatus.SUCCESS)
        }
        return mapper.toDto(payment)
    }

    override fun pay(orderId: UUID, value: Long): PaymentDto {
        return self.payByPaymentId(getPaymentByOrderId(orderId).id!!, value)
    }

    @Caching(
        put = [CachePut(CacheNames.PAYMENTS, key = "#paymentId")],
        evict = [CacheEvict(CacheNames.PAYMENT_COLLECTION, allEntries = true)]
    )
    override fun cancel(paymentId: UUID): PaymentDto {
        val status = self.getPaymentEntity(paymentId).status
        if (status != PaymentStatus.PENDING) {
            val message = messages.find("exceptions.bad-request.payment.status-change", status, PaymentStatus.EXPIRED)
            throw BadRequestException(message)
        }
        return self.updateStatus(paymentId, PaymentStatus.EXPIRED)
    }

    @Caching(
        evict = [
            CacheEvict(CacheNames.PAYMENTS, key = "#paymentId"),
            CacheEvict(CacheNames.PAYMENT_COLLECTION, allEntries = true)]
    )
    override fun delete(paymentId: UUID) {
        log.info { "Deleting payment $paymentId"}
        paymentRepo.deleteById(paymentId)
    }

    @Caching(
        put = [CachePut(CacheNames.PAYMENTS, key = "#result.id")],
        evict = [CacheEvict(CacheNames.PAYMENT_COLLECTION, allEntries = true)]
    )
    override fun createPayment(dto: PaymentDto): PaymentDto {
        log.info { "Creating payment $dto" }
        val payment = mapper.toEntity(dto)
        val created = withHandling { paymentRepo.save(payment) }
        log.info { "Payment ${ created.id } created" }
        return mapper.toDto(created)
    }

    @Caching(
        put = [CachePut(CacheNames.PAYMENTS, key = "#result.id")],
        evict = [CacheEvict(CacheNames.PAYMENT_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun createPayment(orderId: UUID): PaymentDto {
        val order = orderRepo.findById(orderId).orElseThrow {
            NotFoundException(messages.find("exceptions.not-found.order", orderId))
        }
        val value = calculateValue(order)
        return self.createPayment(PaymentDto(
            id = null,
            orderId = order.id!!,
            value = value
        ))
    }

    @Cacheable(CacheNames.PAYMENTS)
    override fun getPayment(id: UUID): PaymentDto {
        return mapper.toDto(self.getPaymentEntity(id))
    }

    override fun getPaymentEntity(paymentId: UUID): Payment {
        return paymentRepo.findById(paymentId).orElseThrow {
            NotFoundException(messages.find("exceptions.not-found.payment", paymentId))
        }
    }

    @Caching(
        put = [CachePut(CacheNames.PAYMENTS, key = "#paymentId")],
        evict = [CacheEvict(CacheNames.PAYMENT_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun updateStatus(paymentId: UUID, status: PaymentStatus, errorReason: String?): PaymentDto {
        val payment = self.getPaymentEntity(paymentId)
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
        return mapper.toDto(updated)
    }

    @Cacheable(CacheNames.PAYMENTS, key = "'by-order-id-and-status:' + #orderId")
    override fun getPaymentByOrderIdAndStatus(orderId: UUID, status: PaymentStatus): PaymentDto {
        return mapper.toDto(paymentRepo.findFirstByOrderIdAndStatus(orderId, status)
            ?: throw NotFoundException(messages.find("exceptions.not-found.payment-by-order", orderId)))
    }

    @Cacheable(CacheNames.PAYMENTS, key = "'by-order-id:' + #orderId")
    override fun getPaymentByOrderId(orderId: UUID): PaymentDto {
        return mapper.toDto(paymentRepo.findFirstByOrderId(orderId)
            ?: throw NotFoundException(messages.find("exceptions.not-found.payment-by-order", orderId)))
    }

    @CachePut(CacheNames.PAYMENTS, key = "#id")
    @Transactional
    override fun updatePayment(id: UUID, update: PaymentDto): PaymentDto {
        val updated = mapper.toEntity(update).apply { this.id = id }
        log.info { "Updating order ${updated.id} to $updated" }
        return withHandling {
            mapper.toDto(paymentRepo.save(updated))
        }
    }

    @Cacheable(CacheNames.PAYMENT_COLLECTION)
    override fun getPayments(pageable: Pageable): Page<PaymentDto> {
        return paymentRepo.findAll(pageable).map { mapper.toDto(it) }
    }

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.MINUTES)
    override fun checkForExpiredPayment() {
        log.info { "Checking for expired payments" }
        val reason = messages.find("notifications.payment.error.expired")
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

    private fun <T> withHandling(block: () -> T): T {
        return try {
            block()
        } catch (_: EntityNotFoundException) {
            throw NotFoundException(messages.find("exceptions.psql.foreign-key"))
        } catch (_: DataIntegrityViolationException) {
            throw BadRequestException(messages.find("exceptions.psql.integrity"))
        }
    }
}