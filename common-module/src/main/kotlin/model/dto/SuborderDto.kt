package com.buoyancy.common.model.dto

import com.buoyancy.common.model.enums.OrderStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class SuborderDto(

    @NotNull var id: UUID,
    @NotNull var orderId: UUID,
    @NotNull var restaurantId: UUID,
    @NotEmpty var items: List<UUID>,
    @NotNull var status: OrderStatus
)