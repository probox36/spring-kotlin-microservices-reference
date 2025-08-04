package com.buoyancy.payment.controller

import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.payment.service.impl.MockPaymentServiceImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/payments")
class PaymentController(
    private val service: MockPaymentServiceImpl
) {

    @GetMapping
    fun getPayments(pageable: Pageable): Page<Payment> {
        return service.getPayments(pageable)
    }

    @GetMapping("/{id}")
    fun getPayment(@PathVariable id: UUID): Payment {
        return service.getPayment(id)
    }

    @GetMapping("/orders/{orderId}")
    fun getPaymentByOrderId(@PathVariable orderId: UUID): Payment {
        return service.getPaymentByOrderId(orderId)
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPayment(@RequestBody payment: Payment): ResourceDto {
        return ResourceDto(200, "Payment created", service.createPayment(payment))
    }

    @PutMapping("/{id}/pay")
    fun pay(@PathVariable id: UUID, @RequestParam value: Long): MessageDto {
        service.pay(id, value)
        return MessageDto(200, "Payment successful")
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: UUID, @RequestParam status: PaymentStatus): ResourceDto {
        val updatedPayment = service.updateStatus(id, status)
        return ResourceDto(200, "Payment status updated", updatedPayment)
    }

    @DeleteMapping("/{id}/delete")
    fun cancel(@PathVariable id: UUID): MessageDto {
        service.cancel(id)
        return MessageDto(200, "Payment cancelled")
    }
}