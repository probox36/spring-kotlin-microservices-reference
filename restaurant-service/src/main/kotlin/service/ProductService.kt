package com.buoyancy.restaurant.service

import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import jakarta.persistence.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ProductService {

    fun createProduct(product: Product)
    fun updateProduct(id: UUID, product: Product): Product
    fun deleteProduct(id: UUID)
    fun getProduct(id: UUID): Product
    fun getProducts(pageable: Pageable): Page<Product>
    fun getProductsByRestaurant(restaurantId: UUID, pageable: Pageable): Page<Product>
    fun updateName(id: UUID, name: String): Product
    fun updatePrice(id: UUID, price: Long): Product
}