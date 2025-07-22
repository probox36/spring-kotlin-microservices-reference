package com.buoyancy.restaurant.repository

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Suborder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SuborderRepository : JpaRepository<Suborder, UUID> {
    fun findByOrder(order: Order): List<Suborder>
    fun findByOrderId(orderId: UUID): List<Suborder>
}