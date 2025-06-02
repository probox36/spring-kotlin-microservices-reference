package com.buoyancy.model.mapper

import com.buoyancy.model.dto.OrderItemDto
import com.buoyancy.model.entity.OrderItem
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

//@Mapper(
//    componentModel = MappingConstants.ComponentModel.SPRING,
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
@Mapper
interface OrderItemMapper {
    fun toDto(orderItem: OrderItem): OrderItemDto
    fun toEntity(dto: OrderItemDto): OrderItem
}