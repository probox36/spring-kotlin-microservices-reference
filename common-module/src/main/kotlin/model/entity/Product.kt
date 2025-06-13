package com.buoyancy.common.model.entity

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "products")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Product (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var name: String,
    var price: Long
)