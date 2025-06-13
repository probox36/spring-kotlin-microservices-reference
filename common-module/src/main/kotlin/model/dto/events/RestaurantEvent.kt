package com.buoyancy.common.model.dto.events

import com.buoyancy.common.model.enums.RestaurantStatus
import com.buoyancy.common.model.interfaces.Event
import java.util.*

data class RestaurantEvent (
    override val type: RestaurantStatus,
    val orderId: UUID
) : Event