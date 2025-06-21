package com.buoyancy.common.model.dto

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.util.*

data class ProductDto(
    @NotNull var id: UUID? = null,
    @NotBlank var name: String,
    @NotNull var price: Long
)