package com.buoyancy.common.model.dto

import com.buoyancy.common.model.enums.OrderStatus
import jakarta.validation.constraints.NotEmpty
import org.jetbrains.annotations.NotNull
import java.util.*

data class OrderDto(
    @NotNull var id: UUID? = null,
    @NotNull var userId: UUID,
    @NotNull var status: OrderStatus,
    @NotEmpty var items: List<ProductDto> = emptyList()
)