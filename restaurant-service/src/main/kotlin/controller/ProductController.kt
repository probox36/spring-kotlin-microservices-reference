package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.mapper.ProductMapper
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.ProductService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/products")
class ProductController {

    @Autowired
    private lateinit var service: ProductService
    @Autowired
    private lateinit var mapper: ProductMapper
    @Autowired
    private lateinit var messages : MessageSource

    private val updatedMessage = messages.get("rest.response.resource.updated")
    private val createdMessage = messages.get("rest.response.resource.created")

    @PostMapping("/{id}/create")
    fun createProduct(@Valid @RequestBody productDto: ProductDto) : ResponseEntity<ResourceDto> {
        val product = mapper.toEntity(productDto)
        service.createProduct(product)

        val status = 201

        return ResponseEntity.status(status).body(
            ResourceDto(201, createdMessage, product)
        )
    }

    @PostMapping("/{id}/update")
    fun updateProduct(
        @PathVariable id: UUID, 
        @Valid @RequestBody productDto: ProductDto
    ) : ResponseEntity<ResourceDto> {
        
        val product = mapper.toEntity(productDto)
        service.updateProduct(id, product)

        val status = 200
        return ResponseEntity.status(status).body(
            ResourceDto(status, updatedMessage, product)
        )
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProduct(@PathVariable id: UUID) : ResponseEntity<MessageDto> {
        service.deleteProduct(id)

        val status = 200
        val message = messages.get("rest.response.resource.deleted", arrayOf(id))
        return ResponseEntity.status(status).body(
            MessageDto(status, message)
        )
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID) : ResponseEntity<Product> {
        val product = service.getProduct(id)
        return ResponseEntity.status(200).body(product)
    }

    @GetMapping()
    fun getProducts(pageable: Pageable) : ResponseEntity<Page<Product>> {
        val product = service.getProducts(pageable)
        return ResponseEntity.status(200).body(product)
    }

    @GetMapping("/restaurant/{restaurantId}")
    fun getProductsByRestaurant(@PathVariable restaurantId: UUID, pageable: Pageable
    ): ResponseEntity<Page<Product>> {
        val products = service.getProductsByRestaurant(restaurantId, pageable)
        return ResponseEntity.status(200).body(products)
    }

    @PostMapping("/{id}/updateName")
    fun updateName(@PathVariable id: UUID, @RequestBody name: String) : ResponseEntity<ResourceDto> {
        val updated = service.updateName(id, name)
        val status = 200
        return ResponseEntity.status(status).body(
            ResourceDto(status, updatedMessage, updated)
        )
    }

    @PostMapping("/{id}/updatePrice")
    fun updatePrice(@PathVariable id: UUID, @RequestBody price: Long) : ResponseEntity<ResourceDto> {
        val updated = service.updatePrice(id, price)
        val status = 200
        return ResponseEntity.status(status).body(
            ResourceDto(status, updatedMessage, updated)
        )
    }
}