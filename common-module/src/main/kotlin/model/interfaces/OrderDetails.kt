package com.buoyancy.common.model.interfaces

import java.util.*

interface OrderDetails {
    val orderId: UUID?
    val userId: UUID?
    val userEmail: String
}