package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.dto.rest.MessageDto
import com.buoyancy.common.model.dto.rest.ResourceDto
import com.buoyancy.common.utils.find
import com.buoyancy.restaurant.service.ProductService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/products")
class ProductController {

    @Autowired
    private lateinit var service: ProductService
    @Autowired
    private lateinit var messages : MessageSource

    private val updatedMessage by lazy { messages.find("rest.response.resource.updated") }
    private val createdMessage by lazy { messages.find("rest.response.resource.created") }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #productDto.restaurantId.toString() == authentication.principal.subject")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@Valid @RequestBody productDto: ProductDto) : ResourceDto<ProductDto> {
        val created = service.createProduct(productDto)
        return ResourceDto(201, createdMessage, created)
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT') and #productDto.restaurantId.toString() == authentication.principal.subject")
    @PostMapping("/{id}/update")
    fun updateProduct(@PathVariable id: UUID, @Valid @RequestBody productDto: ProductDto) : ResourceDto<ProductDto> {
        val updated = service.updateProduct(id, productDto)
        return ResourceDto(200, updatedMessage, updated)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @DeleteMapping("/{id}/delete")
    fun deleteProduct(@PathVariable id: UUID) : MessageDto {
        service.deleteProduct(id)
        val message = messages.find("rest.response.resource.deleted", id)
        return MessageDto(200, message)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: UUID) : ProductDto {
        return service.getProduct(id)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping()
    fun getProducts(pageable: Pageable) : Page<ProductDto> {
        return service.getProducts(pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'RESTAURANT')")
    @GetMapping("/restaurant/{restaurantId}")
    fun getProductsByRestaurant(@PathVariable restaurantId: UUID, pageable: Pageable): Page<ProductDto> {
        return service.getProductsByRestaurant(restaurantId, pageable)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @PostMapping("/{id}/updateName")
    fun updateName(@PathVariable id: UUID, @RequestParam name: String) : ResourceDto<ProductDto> {
        val updated = service.updateName(id, name)
        return ResourceDto(200, updatedMessage, updated)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT')")
    @PostMapping("/{id}/updatePrice")
    fun updatePrice(@PathVariable id: UUID, @RequestParam price: Long) : ResourceDto<ProductDto> {
        val updated = service.updatePrice(id, price)
        return ResourceDto(200, updatedMessage, updated)
    }
}