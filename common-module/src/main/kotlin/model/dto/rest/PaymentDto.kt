package com.buoyancy.common.model.dto.rest

import com.buoyancy.common.model.enums.PaymentStatus
import java.time.LocalDateTime
import java.util.UUID

data class PaymentDto (
    val id: UUID,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val orderId: UUID,
    var value: Long,
    var valuePaid: Long = 0L,
    var time: LocalDateTime = LocalDateTime.now()
)