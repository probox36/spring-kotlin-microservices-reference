package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.entity.Product
import com.buoyancy.restaurant.repository.ProductRepository
import com.buoyancy.restaurant.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

class ProductServiceImpl : ProductService {

    @Autowired
    private lateinit var repo: ProductRepository

    override fun createProduct(product: Product) {
        repo.save(product)
    }

    override fun updateProduct(id: UUID, product: Product) {
        product.id = id
        repo.save(product)
    }

    override fun deleteProduct(id: UUID) {
        repo.deleteById(id)
    }

    override fun getProduct(id: UUID): Product {
        return repo.findById(id).orElseThrow {
            NotFoundException("Product with id $id not found")
        }
    }

    override fun getProducts(pageable: Pageable): List<Product> {
        return repo.findAll(pageable).content
    }

    override fun getProductsByRestaurant(restaurantId: UUID, pageable: Pageable): Page<Product> {
        return repo.findByRestaurantId(restaurantId, pageable)
    }

    override fun updateName(id: UUID, name: String) {
        val product = getProduct(id)
        product.name = name
        repo.save(product)
    }

    override fun updatePrice(id: UUID, price: Long) {
        val product = getProduct(id)
        product.price = price
        repo.save(product)
    }
}