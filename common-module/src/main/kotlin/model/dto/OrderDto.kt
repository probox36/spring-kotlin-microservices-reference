package com.buoyancy.common.model.dto

import com.buoyancy.common.model.enums.OrderStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class OrderDto(
    @NotNull var id: UUID,
    @NotNull var userId: UUID,
    @NotNull var createdAt: LocalDateTime,
    @NotNull var status: OrderStatus,
    @NotEmpty var items: List<UUID> = emptyList()
)