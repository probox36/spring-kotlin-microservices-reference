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
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var userId: UUID,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus,

    @OneToMany(targetEntity = Product::class)
    var items: List<Product> = emptyList()
)