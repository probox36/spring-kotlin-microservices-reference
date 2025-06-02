package com.buoyancy.model.entity

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "items")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OrderItem (

    @Id
    var id: UUID? = null,
    var name: String,
    var price: Long
)