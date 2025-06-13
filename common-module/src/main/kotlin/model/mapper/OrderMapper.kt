package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.OrderDto
import com.buoyancy.common.model.entity.Order
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = [ProductMapper::class],
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OrderMapper {
    fun toEntity(dto: OrderDto): Order
    fun toDto(order: Order): OrderDto
}