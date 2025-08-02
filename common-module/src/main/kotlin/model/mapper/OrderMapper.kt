package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring")
abstract class OrderMapper {

    @Autowired
    protected lateinit var userRepository: JpaRepository<User, UUID>

    @Autowired
    protected lateinit var productRepository: JpaRepository<Product, UUID>

    @Mapping(target = "user", expression = "java(getUserProxy(dto.getUserId()))")
    @Mapping(target = "items", expression = "java(mapIdsToProducts(dto.getItems()))")
    abstract fun toEntity(dto: OrderDto): Order

    @Mapping(target = "userId", expression = "java(order.getUser().getId())")
    @Mapping(target = "items", expression = "java(mapProductsToIds(order.getItems()))")
    abstract fun toDto(order: Order): OrderDto

    protected fun getUserProxy(userId: UUID): User {
        return userRepository.getReferenceById(userId)
    }

    protected fun mapIdsToProducts(productIds: List<UUID>): List<Product> {
        return productIds.map { productRepository.getReferenceById(it) }
    }

    protected fun mapProductsToIds(productIds: List<Product>): List<UUID?> {
        return productIds.map { it.id }
    }
}