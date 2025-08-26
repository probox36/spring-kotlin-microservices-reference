package com.buoyancy.common.model.dto

import com.buoyancy.common.model.enums.OrderStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class OrderDto(
    var id: UUID? = null,
    @NotNull var userId: UUID,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var status: OrderStatus = OrderStatus.CREATED,
    @NotEmpty var items: List<UUID> = emptyList()
): Serializable