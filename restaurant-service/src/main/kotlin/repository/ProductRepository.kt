package com.buoyancy.restaurant.repository

import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : PagingAndSortingRepository<Product, UUID>, JpaRepository<Product, UUID> {

    fun findByRestaurantId(restaurantId: UUID, pageable: Pageable): Page<Product>
    fun findByRestaurant(restaurant: Restaurant, pageable: Pageable): Page<Product>
}