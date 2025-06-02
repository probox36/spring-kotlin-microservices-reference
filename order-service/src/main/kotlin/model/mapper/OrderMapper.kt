package com.buoyancy.model.mapper

import com.buoyancy.model.dto.OrderDto
import com.buoyancy.model.entity.Order
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.InjectionStrategy

//@Mapper(
//    componentModel = MappingConstants.ComponentModel.SPRING,
//    uses = [OrderItemMapper::class],
//    injectionStrategy = InjectionStrategy.CONSTRUCTOR
//)
@Mapper
interface OrderMapper {
    fun toEntity(dto: OrderDto): Order
    fun toDto(order: Order): OrderDto
}