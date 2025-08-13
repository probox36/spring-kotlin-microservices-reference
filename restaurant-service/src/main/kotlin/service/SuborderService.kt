package com.buoyancy.restaurant.service

import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface SuborderService {

    fun getSuborders(pageable: Pageable): Page<Suborder>
    fun getSubordersByRestaurant(restaurantId: UUID, pageable: Pageable): Page<Suborder>
    fun getSubordersByRestaurant(restaurant: Restaurant, pageable: Pageable): Page<Suborder>
    fun markSuborderAsPreparing(id: UUID)
    fun markSuborderAsPreparing(suborder: Suborder)
    fun markSuborderAsReady(id: UUID)
    fun markSuborderAsReady(suborder: Suborder)
    fun postponeSuborder(id: UUID)
    fun postponeSuborder(suborder: Suborder)
    fun getSuborder(id: UUID): Suborder
}