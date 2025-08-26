package com.buoyancy.common.model.entity

import com.buoyancy.common.model.enums.CuisineType
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "products")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Product (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var name: String,
    var price: Long,

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    var restaurant: Restaurant
): Serializable