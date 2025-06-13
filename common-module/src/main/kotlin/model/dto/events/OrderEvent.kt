package com.buoyancy.common.model.dto.events

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.enums.OrderStatus
import com.buoyancy.common.model.interfaces.Event


data class OrderEvent(
    override val type: OrderStatus,
    val orderDto: OrderDto
) : Event