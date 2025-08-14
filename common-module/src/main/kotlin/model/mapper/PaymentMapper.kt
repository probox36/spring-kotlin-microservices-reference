package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.PaymentDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.repository.OrderRepository
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
abstract class PaymentMapper {

    @Autowired
    protected lateinit var orderRepository: OrderRepository

    @Mapping(target = "orderId", expression = "java(payment.getOrder().getId())")
    abstract fun toDto(payment: Payment): PaymentDto

    @Mapping(target = "order", expression = "java(getOrderProxy(dto.getOrderId()))")
    abstract fun toEntity(dto: PaymentDto): Payment

    protected fun getOrderProxy(id: UUID): Order = orderRepository.getReferenceById(id)
}