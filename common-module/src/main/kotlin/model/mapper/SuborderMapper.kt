package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import com.buoyancy.common.model.entity.Suborder
import com.buoyancy.common.repository.OrderRepository
import com.buoyancy.common.repository.ProductRepository
import com.buoyancy.common.repository.RestaurantRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Mapper(componentModel = "spring")
abstract class SuborderMapper {

    @Autowired
    protected lateinit var orderRepo: OrderRepository

    @Autowired
    protected lateinit var productRepo: ProductRepository

    @Autowired
    protected lateinit var restaurantRepo: RestaurantRepository

    @Mapping(target = "order", expression = "java(getOrderProxy(dto.getOrderId()))")
    @Mapping(target = "restaurant", expression = "java(getRestaurantProxy(dto.getRestaurantId()))")
    @Mapping(target = "items", expression = "java(mapIdsToProducts(dto.getItems()))")
    abstract fun toEntity(dto: SuborderDto): Suborder

    @Mapping(target = "orderId", expression = "java(suborder.getOrder().getId())")
    @Mapping(target = "restaurantId", expression = "java(suborder.getRestaurant().getId())")
    @Mapping(target = "items", expression = "java(mapProductsToIds(suborder.getItems()))")
    abstract fun toDto(suborder: Suborder): SuborderDto

    protected fun getOrderProxy(orderId: UUID): Order {
        return orderRepo.getReferenceById(orderId)
    }

    protected fun getRestaurantProxy(orderId: UUID): Restaurant {
        return restaurantRepo.getReferenceById(orderId)
    }

    protected fun mapIdsToProducts(productIds: List<UUID>): List<Product> {
        return productIds.map { productRepo.getReferenceById(it) }
    }

    protected fun mapProductsToIds(productIds: List<Product>): List<UUID?> {
        return productIds.map { it.id }
    }
}