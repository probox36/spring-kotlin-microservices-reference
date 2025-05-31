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
    var userId: String,
    var status: OrderStatus,

    @OneToMany
    var items: List<OrderItem> = emptyList()
)