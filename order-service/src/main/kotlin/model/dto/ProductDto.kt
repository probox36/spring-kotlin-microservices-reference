package com.buoyancy.model.dto

import java.util.*

data class ProductDto(
    var id: UUID? = null,
    var name: String,
    var price: Long
)