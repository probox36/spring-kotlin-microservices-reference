package com.buoyancy.common.model.entity

import com.buoyancy.common.model.enums.OrderStatus
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "orders")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,
    var createdAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus,

    @OneToMany(targetEntity = Product::class)
    var items: List<Product> = emptyList()
)