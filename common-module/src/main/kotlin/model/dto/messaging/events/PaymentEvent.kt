package com.buoyancy.common.model.dto.messaging.events

import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.model.interfaces.Event
import com.buoyancy.common.model.interfaces.OrderDetails
import jakarta.validation.constraints.Email
import org.jetbrains.annotations.NotNull
import java.util.*

data class PaymentEvent(
    @NotNull override val type: PaymentStatus,
    @NotNull override val orderId: UUID,
    @NotNull override val userId: UUID,
    @Email override val userEmail: String,
    val errorReason: String? = null
): Event, OrderDetails {
    constructor(orderDetails: OrderDetails, type: PaymentStatus, errorReason: String? = null) : this (
        userEmail = orderDetails.userEmail,
        orderId = orderDetails.orderId,
        userId = orderDetails.userId,
        type = type,
        errorReason = errorReason
    )
}