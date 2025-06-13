package com.buoyancy.common.model.dto.events

import com.buoyancy.common.model.enums.PaymentStatus
import com.buoyancy.common.model.interfaces.Event
import java.util.*

data class PaymentEvent(
    override val type: PaymentStatus,
    val orderId: UUID
): Event