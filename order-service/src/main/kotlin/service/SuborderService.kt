package com.buoyancy.order.service

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.SuborderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface SuborderService {

    fun createSuborder(suborder: SuborderDto): SuborderDto
    fun getSuborder(id: UUID): SuborderDto
    fun getSuborderEntity(id: UUID): Suborder
    fun deleteSuborder(id: UUID)
    fun updateSuborder(id: UUID, suborder: SuborderDto): SuborderDto
    fun splitToSuborders(orderId: UUID): List<SuborderDto>
    fun updateStatus(id: UUID, status: SuborderStatus): SuborderDto
    fun getSubordersByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<SuborderDto>
    fun getSubordersByRestaurantIdAndStatus(restaurantId: UUID, status: SuborderStatus, pageable: Pageable): Page<SuborderDto>
    fun getSuborders(pageable: Pageable): Page<SuborderDto>
}