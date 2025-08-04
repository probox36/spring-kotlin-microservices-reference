package com.buoyancy.common.model.entity

import com.buoyancy.common.model.enums.SuborderStatus
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "suborders")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Suborder (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order,

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    var restaurant: Restaurant,

    @OneToMany(targetEntity = Product::class)
    @JoinTable(name="suborders_products",
        joinColumns = [JoinColumn(name="order_id", referencedColumnName="id")],
        inverseJoinColumns = [JoinColumn(name="product_id", referencedColumnName="id")],
    )
    var items: MutableList<Product>,

    @Enumerated(EnumType.STRING)
    var status: SuborderStatus
)