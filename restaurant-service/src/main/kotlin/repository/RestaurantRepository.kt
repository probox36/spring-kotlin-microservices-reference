package com.buoyancy.restaurant.repository

import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Restaurant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface RestaurantRepository : PagingAndSortingRepository<Order, Restaurant>