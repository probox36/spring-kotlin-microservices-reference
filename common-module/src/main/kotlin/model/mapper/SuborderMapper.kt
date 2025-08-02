package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired

@Mapper(componentModel = "spring")
abstract class SuborderMapper {

    @Autowired
    protected lateinit var orderRepository: JpaRepository<Order, UUID>

    @Autowired
    protected lateinit var productRepository: JpaRepository<Product, UUID>

    @Autowired
    protected lateinit var restaurantRepository: JpaRepository<Restaurant, UUID>

    @Mapping(target = "order", expression = "java(getOrderProxy(dto.getOrderId()))")
    @Mapping(target = "restaurant", expression = "java(getRestaurantProxy(dto.getRestaurantId()))")
    @Mapping(target = "items", expression = "java(mapIdsToProducts(dto.getItems()))")
    abstract fun toEntity(dto: SuborderDto): Suborder

    @Mapping(target = "orderId", expression = "java(suborder.getOrder().getId())")
    @Mapping(target = "restaurantId", expression = "java(suborder.getRestaurant().getId())")
    @Mapping(target = "items", expression = "java(mapProductsToIds(suborder.getItems()))")
    abstract fun toDto(suborder: Suborder): SuborderDto

    protected fun getOrderProxy(orderId: UUID): Order {
        return orderRepository.getReferenceById(orderId)
    }

    protected fun getRestaurantProxy(orderId: UUID): Restaurant {
        return restaurantRepository.getReferenceById(orderId)
    }

    protected fun mapIdsToProducts(productIds: List<UUID>): List<Product> {
        return productIds.map { productRepository.getReferenceById(it) }
    }

    protected fun mapProductsToIds(productIds: List<Product>): List<UUID?> {
        return productIds.map { it.id }
    }
}