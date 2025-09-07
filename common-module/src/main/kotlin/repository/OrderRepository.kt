package com.buoyancy.common.repository

import com.buoyancy.common.model.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : JpaRepository<Order, UUID>, PagingAndSortingRepository<Order, UUID>