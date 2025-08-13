package com.buoyancy.common.model.dto.rest

data class ResourceDto<T> (
    val statusCode: Int,
    val message: String,
    val entity: T
)