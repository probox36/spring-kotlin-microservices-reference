package com.buoyancy.model.dto

import com.buoyancy.model.entity.OrderStatus
import java.util.*

data class OrderDto(
    var id: UUID? = null,
    var userId: UUID,
    var status: OrderStatus,
    var items: List<ProductDto> = emptyList()
)