package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.repository.ProductRepository
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.ProductService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProductServiceImpl : ProductService {

    private val log = KotlinLogging.logger {}
    @Autowired
    private lateinit var repo: ProductRepository
    @Autowired
    private lateinit var messages: MessageSource

    @Transactional
    override fun createProduct(product: Product): Product {
        if (product.id != null && repo.existsById(product.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.product", product.id!!)
            throw ConflictException(conflictMessage)
        }
        log.info { "Creating product $product" }
        return repo.save(product)
    }

    override fun updateProduct(id: UUID, product: Product): Product {
        product.id = id
        repo.save(product)
        log.info { "Updated product $product" }
        return product
    }

    override fun deleteProduct(id: UUID) {
        log.info { "Deleting product $id" }
        repo.deleteById(id)
    }

    override fun getProduct(id: UUID): Product {
        return repo.findById(id).orElseThrow {
            NotFoundException("Product with id $id not found")
        }
    }

    override fun getProducts(pageable: Pageable): Page<Product> {
        return repo.findAll(pageable)
    }

    override fun getProductsByRestaurant(restaurantId: UUID, pageable: Pageable): Page<Product> {
        return repo.findByRestaurantId(restaurantId, pageable)
    }

    override fun updateName(id: UUID, name: String) : Product {
        val product = getProduct(id)
        product.name = name
        repo.save(product)
        log.info { "Updated name of product ${product.id} from ${product.name} to $name" }
        return product
    }

    override fun updatePrice(id: UUID, price: Long): Product {
        val product = getProduct(id)
        product.price = price
        repo.save(product)
        log.info { "Updated price of product ${product.id} from ${product.price} to $price" }
        return product
    }
}