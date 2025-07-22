package com.buoyancy.common.model.dto.rest

data class ResourceDto (
    val statusCode: Int,
    val message: String,
    val entity: Any
)