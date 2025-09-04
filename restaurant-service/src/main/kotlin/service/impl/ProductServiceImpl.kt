package com.buoyancy.restaurant.service.impl

import com.buoyancy.common.exceptions.BadRequestException
import com.buoyancy.common.exceptions.ConflictException
import com.buoyancy.common.exceptions.NotFoundException
import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.mapper.ProductMapper
import com.buoyancy.common.repository.ProductRepository
import com.buoyancy.common.utils.get
import com.buoyancy.restaurant.service.ProductService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProductServiceImpl : ProductService {

    private val log = KotlinLogging.logger {}
    @Autowired
    private lateinit var self: ProductService
    @Autowired
    private lateinit var repo: ProductRepository
    @Autowired
    private lateinit var messages: MessageSource
    @Autowired
    private lateinit var mapper: ProductMapper

    @Caching(
        put = [CachePut(CacheNames.PRODUCTS, key = "#result.id")],
        evict = [CacheEvict(CacheNames.PRODUCT_COLLECTION, allEntries = true)]
    )
    @Transactional
    override fun createProduct(dto: ProductDto): ProductDto {
        if (dto.id != null && repo.existsById(dto.id!!)) {
            val conflictMessage = messages.get("exceptions.conflict.product", dto.id!!)
            throw ConflictException(conflictMessage)
        }
        val product = mapper.toEntity(dto)
        val saved = withHandling { repo.save(product) }
        log.info { "Created product $saved" }
        return mapper.toDto(saved)
    }

    @Caching(
        put = [CachePut(CacheNames.PRODUCTS, key = "#id")],
        evict = [CacheEvict(CacheNames.PRODUCT_COLLECTION, allEntries = true)]
    )
    override fun updateProduct(id: UUID, dto: ProductDto): ProductDto {
        val product = mapper.toEntity(dto)
        product.id = id
        val updated = withHandling { repo.save(product) }
        log.info { "Updated product $id to $updated" }
        return mapper.toDto(updated)
    }

    @Caching(
        evict = [CacheEvict(CacheNames.PRODUCTS, key = "#id"),
            CacheEvict(CacheNames.PRODUCT_COLLECTION, allEntries = true)]
    )
    override fun deleteProduct(id: UUID) {
        log.info { "Deleting product $id" }
        repo.deleteById(id)
    }

    @Cacheable(CacheNames.PRODUCTS)
    override fun getProduct(id: UUID): ProductDto {
        return mapper.toDto(self.getProductEntity(id))
    }

    override fun getProductEntity(id: UUID): Product {
        return repo.findById(id).orElseThrow {
            NotFoundException(messages.get("exceptions.not-found.product", id))
        }
    }

    @Cacheable(CacheNames.PRODUCT_COLLECTION)
    override fun getProducts(pageable: Pageable): Page<ProductDto> {
        return repo.findAll(pageable).map { mapper.toDto(it) }
    }

    @Cacheable(CacheNames.PRODUCT_COLLECTION, key = "{#restaurantId, #pageable}")
    override fun getProductsByRestaurant(restaurantId: UUID, pageable: Pageable): Page<ProductDto> {
        return repo.findByRestaurantId(restaurantId, pageable).map { mapper.toDto(it) }
    }

    @Caching(
        put = [CachePut(CacheNames.PRODUCTS, key = "#id")],
        evict = [CacheEvict(CacheNames.PRODUCT_COLLECTION, allEntries = true)]
    )
    override fun updateName(id: UUID, name: String) : ProductDto {
        val product = self.getProductEntity(id)
        product.name = name
        repo.save(product)
        log.info { "Updated name of product ${product.id} from ${product.name} to $name" }
        return mapper.toDto(product)
    }

    @Caching(
        put = [CachePut(CacheNames.PRODUCTS, key = "#id")],
        evict = [CacheEvict(CacheNames.PRODUCT_COLLECTION, allEntries = true)]
    )
    override fun updatePrice(id: UUID, price: Long): ProductDto {
        val product = self.getProductEntity(id)
        product.price = price
        repo.save(product)
        log.info { "Updated price of product ${product.id} from ${product.price} to $price" }
        return mapper.toDto(product)
    }

    private fun <T> withHandling(block: () -> T): T {
        return try {
            block()
        } catch (_: EntityNotFoundException) {
            throw NotFoundException(messages.get("exceptions.psql.foreign-key"))
        } catch (_: DataIntegrityViolationException) {
            throw BadRequestException(messages.get("exceptions.psql.integrity"))
        }
    }
}