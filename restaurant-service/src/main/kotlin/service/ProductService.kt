package com.buoyancy.restaurant.service

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import jakarta.persistence.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ProductService {

    fun createProduct(product: ProductDto): ProductDto
    fun updateProduct(id: UUID, product: ProductDto): ProductDto
    fun deleteProduct(id: UUID)
    fun getProduct(id: UUID): ProductDto
    fun getProductEntity(id: UUID): Product
    fun getProducts(pageable: Pageable): Page<ProductDto>
    fun getProductsByRestaurant(restaurantId: UUID, pageable: Pageable): Page<ProductDto>
    fun updateName(id: UUID, name: String): ProductDto
    fun updatePrice(id: UUID, price: Long): ProductDto
}