package com.buoyancy.common.repository

import com.buoyancy.common.model.entity.Payment
import com.buoyancy.common.model.enums.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface PaymentRepository : JpaRepository<Payment, UUID>, PagingAndSortingRepository<Payment, UUID> {

    fun findByStatusAndTimeBefore(
        status: PaymentStatus? = PaymentStatus.PENDING,
        time: LocalDateTime? = LocalDateTime.now().minusMinutes(5)
    ): List<Payment>

    fun findFirstByOrderIdAndStatus(id: UUID, status: PaymentStatus): Payment?
    fun findFirstByOrderId(id: UUID): Payment?
    fun findByOrderId(id: UUID): List<Payment>
}