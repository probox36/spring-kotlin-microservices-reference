package com.buoyancy.common.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.util.*

data class UserDto (
    var id: UUID? = null,
    @NotBlank var name: String,
    @NotBlank var lastName: String,
    @Email var email: String
)