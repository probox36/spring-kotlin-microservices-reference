package com.buoyancy.common.model.dto.messaging

import com.buoyancy.common.model.interfaces.OrderDetails
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.util.*

data class Notification (
    @Email override val userEmail: String,
    @NotNull override val orderId: UUID?,
    @NotNull override val userId: UUID?,
    @NotBlank val message: String
): OrderDetails {
    constructor(event: OrderDetails, message: String) : this(
        userEmail = event.userEmail,
        orderId = event.orderId,
        userId = event.userId,
        message = message
    )
}