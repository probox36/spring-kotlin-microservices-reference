package com.buoyancy.common.model.dto

import com.buoyancy.common.model.enums.SuborderStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.util.*

data class SuborderDto(
    var id: UUID? = null,
    @NotNull var orderId: UUID,
    @NotNull var restaurantId: UUID,
    @NotEmpty var items: List<UUID>,
    var status: SuborderStatus = SuborderStatus.CREATED
): Serializable