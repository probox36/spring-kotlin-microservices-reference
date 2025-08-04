package com.buoyancy.payment.repository

import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface PaymentRepository : JpaRepository<Payment, UUID>, PagingAndSortingRepository<Payment, UUID> {
    
    fun findByStatusAndCreatedTimeBefore(
        status: PaymentStatus? = PaymentStatus.PENDING,
        time: LocalDateTime? = LocalDateTime.now().minusMinutes(20)
    ): List<Payment>

    fun findByOrderId(id: UUID): List<Payment>
}