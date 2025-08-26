package com.buoyancy.common.model.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.io.Serializable
import java.util.*

data class ProductDto(
    var id: UUID? = null,
    @NotBlank var name: String,
    @NotNull @Min(0) var price: Long,
    @NotNull var restaurantId: UUID
): Serializable