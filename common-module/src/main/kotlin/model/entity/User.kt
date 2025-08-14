package com.buoyancy.common.model.entity

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "users")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class User (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var name: String,
    var lastName: String,
    var email: String
): Serializable