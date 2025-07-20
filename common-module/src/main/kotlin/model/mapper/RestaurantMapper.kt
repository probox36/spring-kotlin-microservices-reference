package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.RestaurantDto
import com.buoyancy.common.model.entity.Restaurant
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface RestaurantMapper {
    fun toDto(product: Restaurant): RestaurantDto
    fun toEntity(dto: RestaurantDto): Restaurant
}