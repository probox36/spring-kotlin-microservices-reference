package com.buoyancy.common.model.dto.messaging.events

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.interfaces.Event
import com.buoyancy.common.model.interfaces.OrderDetails
import jakarta.validation.constraints.Email
import org.jetbrains.annotations.NotNull
import java.util.*

data class OrderEvent(
    @NotNull override val type: OrderStatus,
    @NotNull override val orderId: UUID,
    @NotNull override val userId: UUID,
    @Email override val userEmail: String
) : Event, OrderDetails {

    constructor(orderDetails: OrderDetails, type: OrderStatus) : this(
        userEmail = orderDetails.userEmail,
        orderId = orderDetails.orderId,
        userId = orderDetails.userId,
        type = type
    )

    constructor(order: Order, type: OrderStatus) : this(
        userEmail = order.user.email,
        orderId = order.id ?: throw IllegalArgumentException("Order ID must not be null"),
        userId = order.user.id ?: throw IllegalArgumentException("User ID must not be null"),
        type = type
    )
}