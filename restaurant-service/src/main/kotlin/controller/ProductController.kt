package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.mapper.ProductMapper
import com.buoyancy.restaurant.service.ProductService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/products")
class ProductController {

    @Autowired
    private lateinit var service: ProductService
    @Autowired
    private lateinit var mapper: ProductMapper

    @PostMapping("/{id}/create")
    fun createProduct(@Valid @RequestBody productDto: ProductDto) {
        val product = mapper.toEntity(productDto)
        service.createProduct(product)
    }

    @PostMapping("/{id}/update")
    fun updateProduct(@PathVariable id: UUID, @Valid @RequestBody productDto: ProductDto) {
        val product = mapper.toEntity(productDto)
        service.updateProduct(id, product)
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProduct(@PathVariable id: UUID) {
        service.deleteProduct(id)
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID): Product {
        return service.getProduct(id)
    }

    @GetMapping()
    fun getProducts(pageable: Pageable): List<Product> {
        return service.getProducts(pageable)
    }

    @GetMapping("/restaurant/{restaurantId}")
    fun getProductsByRestaurant(@PathVariable restaurantId: UUID, pageable: Pageable): Page<Product> {
        return service.getProductsByRestaurant(restaurantId, pageable)
    }

    @PostMapping("/{id}/updateName")
    fun updateName(@PathVariable id: UUID, @RequestBody name: String) {
        service.updateName(id, name)
    }

    @PostMapping("/{id}/updatePrice")
    fun updatePrice(@PathVariable id: UUID, @RequestBody price: Long) {
        service.updatePrice(id, price)
    }
}