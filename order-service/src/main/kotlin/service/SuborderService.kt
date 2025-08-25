package com.buoyancy.order.service

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.SuborderStatus
import java.util.*

interface SuborderService {

    fun createSuborder(suborder: SuborderDto): SuborderDto
    fun getSuborder(id: UUID): SuborderDto
    fun splitToSuborders(orderId: UUID): List<SuborderDto>
    fun updateStatus(id: UUID, status: SuborderStatus): SuborderDto
}