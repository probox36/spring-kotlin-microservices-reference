package com.buoyancy.common.model.entity

import com.buoyancy.common.model.enums.OrderStatus
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "restaurants")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class Suborder (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID,

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order,

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    var restaurant: Restaurant,

    @OneToMany(targetEntity = Product::class)
    @JoinTable(name="order_products",
        joinColumns = [JoinColumn(name="order_id", referencedColumnName="id")],
        inverseJoinColumns = [JoinColumn(name="product_id", referencedColumnName="id")],
    )
    var items: List<Product>,

    var status: OrderStatus
)