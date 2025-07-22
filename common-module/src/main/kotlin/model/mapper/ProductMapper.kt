package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.entity.Restaurant
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
abstract class ProductMapper {

    @Autowired
    protected lateinit var restaurantRepository: JpaRepository<Restaurant, UUID>

    @Mapping(target = "restaurantId", expression = "java(product.getRestaurant().getId())")
    abstract fun toDto(product: Product): ProductDto

    @Mapping(target = "restaurant", expression = "java(getRestaurantProxy(dto.getRestaurantId()))")
    abstract fun toEntity(dto: ProductDto): Product

    protected fun getRestaurantProxy(id: UUID): Restaurant = restaurantRepository.getReferenceById(id)
}