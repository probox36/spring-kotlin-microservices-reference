package com.buoyancy.restaurant.service

import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.model.enums.SuborderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface SuborderService {

    fun getSuborders(pageable: Pageable): Page<SuborderDto>
    fun getSubordersByRestaurantIdAndStatus(restaurantId: UUID, status: SuborderStatus, pageable: Pageable): Page<SuborderDto>
    fun getSubordersByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<SuborderDto>
    fun markSuborderAsPreparing(id: UUID): SuborderDto
    fun markSuborderAsPreparing(suborder: SuborderDto): SuborderDto
    fun markSuborderAsReady(id: UUID): SuborderDto
    fun markSuborderAsReady(suborder: SuborderDto): SuborderDto
    fun postponeSuborder(id: UUID): SuborderDto
    fun postponeSuborder(suborder: SuborderDto): SuborderDto
    fun getSuborder(id: UUID): SuborderDto
    fun getSuborderEntity(id: UUID): Suborder
}