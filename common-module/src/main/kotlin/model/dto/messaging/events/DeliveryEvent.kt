package com.buoyancy.common.model.dto.messaging.events

import com.buoyancy.common.model.enums.DeliveryStatus
import com.buoyancy.common.model.interfaces.Event
import com.buoyancy.common.model.interfaces.OrderDetails
import jakarta.validation.constraints.Email
import org.jetbrains.annotations.NotNull
import java.util.*

data class DeliveryEvent (
    @NotNull override val type: DeliveryStatus,
    @NotNull override val orderId: UUID?,
    @NotNull override val userId: UUID?,
    @Email override val userEmail: String
) : Event, OrderDetails {
    constructor(orderDetails: OrderDetails, type: DeliveryStatus) : this (
        userEmail = orderDetails.userEmail,
        orderId = orderDetails.orderId,
        userId = orderDetails.userId,
        type = type
    )
}