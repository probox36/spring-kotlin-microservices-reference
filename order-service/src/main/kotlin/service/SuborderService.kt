package com.buoyancy.order.service

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.SuborderStatus
import java.util.*

interface SuborderService {

    fun createSuborder(order: Suborder): Suborder
    fun getSuborder(id: UUID): Suborder
    fun splitToSuborders(order: Order): List<Suborder>
    fun updateStatus(id: UUID, status: SuborderStatus)
}