package com.buoyancy.common.model.entity

import com.buoyancy.common.model.enums.PaymentStatus
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "payments")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Payment (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @OneToOne
    @JoinColumn(name = "order_id")
    var order: Order,

    var value: Long,
    var valuePaid: Long = 0L,
    var time: LocalDateTime = LocalDateTime.now()
)