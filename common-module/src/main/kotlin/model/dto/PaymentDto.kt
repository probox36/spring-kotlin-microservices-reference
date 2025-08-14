package com.buoyancy.common.model.dto

import com.buoyancy.common.model.enums.PaymentStatus
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.UUID

data class PaymentDto (
    val id: UUID? = null,
    val status: PaymentStatus = PaymentStatus.PENDING,
    @NotNull val orderId: UUID,
    @Min(0) var value: Long,
    @Min(0)var valuePaid: Long = 0L,
    var time: LocalDateTime = LocalDateTime.now()
)