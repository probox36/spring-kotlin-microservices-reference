package com.buoyancy.common.model.dto.rest

import org.springframework.http.HttpStatusCode

data class MessageDto (
    val statusCode: HttpStatusCode,
    val message: String
)