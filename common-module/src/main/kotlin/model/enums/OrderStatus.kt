package com.buoyancy.common.model.enums

import com.buoyancy.common.model.interfaces.Status

enum class OrderStatus : Status {
    CREATED, PAID, PREPARING, POSTPONED, READY, CANCELLED, CLOSED
}