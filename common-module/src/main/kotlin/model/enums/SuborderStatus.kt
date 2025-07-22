package com.buoyancy.common.model.enums

import com.buoyancy.common.model.interfaces.Status

enum class SuborderStatus : Status {
    CREATED, PREPARING, POSTPONED, READY, CANCELLED
}