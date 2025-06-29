package com.buoyancy.common.model.dto.messaging.events

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.interfaces.Event
import com.buoyancy.common.model.interfaces.Message
import jakarta.validation.constraints.Email
import org.jetbrains.annotations.NotNull
import java.util.*


data class OrderEvent(
    @NotNull override val type: OrderStatus,
    @NotNull override val orderId: UUID,
    @NotNull override val userId: UUID,
    @Email override val userEmail: String
) : Event, Message {

    constructor(message: Message, type: OrderStatus) : this(
        userEmail = message.userEmail,
        orderId = message.orderId,
        userId = message.userId,
        type = type
    )

    constructor(order: Order, type: OrderStatus) : this(
        userEmail = order.user.email,
        orderId = order.id,
        userId = order.user.id,
        type = type
    )
}