package com.buoyancy.model.dto

import java.util.*

data class OrderItemDto(
    var id: UUID? = null,
    var name: String,
    var price: Long
)