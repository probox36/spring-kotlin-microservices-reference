package com.buoyancy.payment.controller

import com.buoyancy.common.model.dto.PaymentDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.utils.get
import com.buoyancy.payment.service.impl.MockPaymentServiceImpl
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class PaymentController {

    @Autowired
    private lateinit var service: MockPaymentServiceImpl
    @Autowired
    private lateinit var messages : MessageSource

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping
    fun getPayments(pageable: Pageable): Page<PaymentDto> {
        return service.getPayments(pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/{id}")
    fun getPayment(@PathVariable id: UUID): PaymentDto {
        return service.getPayment(id)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/by-order-id/{orderId}")
    fun getPaymentByOrderId(@PathVariable orderId: UUID): PaymentDto {
        return service.getPaymentByOrderId(orderId)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPayment(@Valid @RequestBody paymentDto: PaymentDto): ResourceDto<PaymentDto> {
        val created = service.createPayment(paymentDto)
        return ResourceDto(200, messages.get("rest.response.payment.created"), created)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping("/{paymentId}/pay-by-payment-id")
    fun payByPaymentId(@PathVariable paymentId: UUID, @RequestParam value: Long): MessageDto {
        service.payByPaymentId(paymentId, value)
        return MessageDto(200, messages.get("rest.response.payment.success"))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @PutMapping("/{orderId}/pay")
    fun pay(@PathVariable orderId: UUID, @RequestParam value: Long): MessageDto {
        service.pay(orderId, value)
        return MessageDto(200, messages.get("rest.response.payment.success"))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/update")
    fun updatePayment(@PathVariable id: UUID, @Valid @RequestBody paymentDto: PaymentDto): ResourceDto<PaymentDto> {
        val message = messages.get("rest.response.payment.updated", id)
        return ResourceDto(200, message, service.updatePayment(id, paymentDto))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: UUID, @RequestParam status: PaymentStatus): ResourceDto<PaymentDto> {
        val updated = service.updateStatus(id, status)
        val message = messages.get("rest.response.payment.status-updated", id, status)
        return ResourceDto(200, message, updated)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/cancel")
    fun cancel(@PathVariable id: UUID): MessageDto {
        service.cancel(id)
        return MessageDto(200, messages.get("rest.response.payment.cancelled", id))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    fun delete(@PathVariable id: UUID): MessageDto {
        service.delete(id)
        return MessageDto(200, messages.get("rest.response.payment.deleted", id))
    }
}