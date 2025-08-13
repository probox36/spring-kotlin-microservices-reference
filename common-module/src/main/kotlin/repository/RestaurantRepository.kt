package com.buoyancy.common.repository

import com.buoyancy.common.model.entity.Restaurant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantRepository : PagingAndSortingRepository<Restaurant, UUID>, JpaRepository<Restaurant, UUID>