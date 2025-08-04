package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.rest.PaymentDto
import com.buoyancy.common.model.entity.Order
import com.buoyancy.common.model.entity.Payment
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
abstract class PaymentMapper {

    @Autowired
    protected lateinit var orderRepository: JpaRepository<Order, UUID>

    @Mapping(target = "orderId", expression = "java(payment.getOrder().getId())")
    abstract fun toDto(payment: Payment): PaymentDto

    @Mapping(target = "order", expression = "java(getOrderProxy(dto.getOrderId()))")
    abstract fun toEntity(dto: PaymentDto): Payment

    protected fun getOrderProxy(id: UUID): Order = orderRepository.getReferenceById(id)
}