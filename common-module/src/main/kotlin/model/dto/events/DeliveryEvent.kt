package com.buoyancy.common.model.dto.events

import com.buoyancy.common.model.enums.DeliveryStatus
import com.buoyancy.common.model.interfaces.Event
import java.util.*

data class DeliveryEvent (
    override val type: DeliveryStatus,
    val orderId: UUID
) : Event