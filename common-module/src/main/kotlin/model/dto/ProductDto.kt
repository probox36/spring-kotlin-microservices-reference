package com.buoyancy.common.model.dto

import jakarta.validation.constraints.NotEmpty
import org.jetbrains.annotations.NotNull
import java.util.*

data class ProductDto(
    @NotNull var id: UUID? = null,
    @NotEmpty var name: String,
    @NotNull var price: Long
)