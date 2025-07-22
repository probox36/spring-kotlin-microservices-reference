package com.buoyancy.restaurant.repository

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SuborderRepository : PagingAndSortingRepository<Suborder, UUID>, JpaRepository<Suborder, UUID> {
    fun findByOrder(order: Order): List<Suborder>
    fun findByOrderId(orderId: UUID): List<Suborder>
    fun findByRestaurant(restaurant: Restaurant, pageable: Pageable): Page<Suborder>
    fun findByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Suborder>
}