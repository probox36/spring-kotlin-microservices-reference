package com.buoyancy.common.model.dto.messaging.events

import com.buoyancy.common.model.enums.SuborderStatus
import com.buoyancy.common.model.interfaces.Event
import org.jetbrains.annotations.NotNull
import java.util.*

data class SuborderEvent(
    @NotNull override val type: SuborderStatus,
    @NotNull val suborderId: UUID
) : Event