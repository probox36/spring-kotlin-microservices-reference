package com.buoyancy.common.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.util.*

data class RestaurantDto (
    val id: UUID? = null,
    @NotBlank val name: String,
    @NotBlank val address: String,
    @NotBlank val phoneNumber: String,
    @Email val email: String,
    @NotNull val cuisineType: String,
    val operating: Boolean? = true
)
