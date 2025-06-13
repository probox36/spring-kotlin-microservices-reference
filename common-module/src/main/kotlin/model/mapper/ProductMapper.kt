package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.ProductDto
import com.buoyancy.common.model.entity.Product
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface ProductMapper {
    fun toDto(product: Product): ProductDto
    fun toEntity(dto: ProductDto): Product
}