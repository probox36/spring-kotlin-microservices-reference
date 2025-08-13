package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.model.mapper.ProductMapper
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.ProductService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
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

    private val updatedMessage by lazy { messages.get("rest.response.resource.updated") }
    private val createdMessage by lazy { messages.get("rest.response.resource.created") }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@Valid @RequestBody productDto: ProductDto) : ResourceDto<ProductDto> {
        val created = service.createProduct(mapper.toEntity(productDto))
        return ResourceDto(201, createdMessage, mapper.toDto(created))
    }

    @PostMapping("/{id}/update")
    fun updateProduct(@PathVariable id: UUID, @Valid @RequestBody productDto: ProductDto) : ResourceDto<ProductDto> {
        val updated = service.updateProduct(id, mapper.toEntity(productDto))
        return ResourceDto(200, updatedMessage, mapper.toDto(updated))
    }

    @DeleteMapping("/{id}/delete")
    fun deleteProduct(@PathVariable id: UUID) : MessageDto {
        service.deleteProduct(id)
        val message = messages.get("rest.response.resource.deleted", id)
        return MessageDto(200, message)
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID) : ProductDto {
        val product = service.getProduct(id)
        return mapper.toDto(product)
    }

    @GetMapping()
    fun getProducts(pageable: Pageable) : Page<ProductDto> {
        val products = service.getProducts(pageable)
        return products.map { mapper.toDto(it) }
    }

    @GetMapping("/restaurant/{restaurantId}")
    fun getProductsByRestaurant(@PathVariable restaurantId: UUID, pageable: Pageable): Page<ProductDto> {
        val products = service.getProductsByRestaurant(restaurantId, pageable)
        return products.map { mapper.toDto(it) }
    }

    @PostMapping("/{id}/updateName")
    fun updateName(@PathVariable id: UUID, @RequestParam name: String) : ResourceDto<ProductDto> {
        val updated = service.updateName(id, name)
        return ResourceDto(200, updatedMessage, mapper.toDto(updated))
    }

    @PostMapping("/{id}/updatePrice")
    fun updatePrice(@PathVariable id: UUID, @RequestParam price: Long) : ResourceDto<ProductDto> {
        val updated = service.updatePrice(id, price)
        return ResourceDto(200, updatedMessage, mapper.toDto(updated))
    }
}