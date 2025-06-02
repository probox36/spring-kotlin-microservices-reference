package com.buoyancy.model.entity

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "orders")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Order(

    @Id
    var id: UUID? = null,
    var userId: UUID,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus,

    @OneToMany(targetEntity = OrderItem::class)
    var items: List<OrderItem> = emptyList()
)