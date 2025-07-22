package com.buoyancy.common.model.dto.rest

import jakarta.validation.constraints.Email

data class EmailUpdateDto (
    @Email val email: String
)