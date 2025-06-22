package com.buoyancy.common.model.dto.rest

import org.springframework.http.HttpStatusCode

data class ErrorDto (
    val statusCode: HttpStatusCode,
    val message: String
)