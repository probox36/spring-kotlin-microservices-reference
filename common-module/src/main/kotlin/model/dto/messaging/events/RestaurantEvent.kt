package com.buoyancy.common.model.dto.messaging.events

import com.buoyancy.common.model.enums.RestaurantStatus
import com.buoyancy.common.model.interfaces.Event
import com.buoyancy.common.model.interfaces.Message
import jakarta.validation.constraints.Email
import org.jetbrains.annotations.NotNull
import java.util.*

data class RestaurantEvent (
    @NotNull override val type: RestaurantStatus,
    @NotNull override val orderId: UUID,
    @NotNull override val userId: UUID,
    @Email override val userEmail: String
) : Event, Message {
    constructor(message: Message, type: RestaurantStatus) : this(
        userEmail = message.userEmail,
        orderId = message.orderId,
        userId = message.userId,
        type = type
    )
}