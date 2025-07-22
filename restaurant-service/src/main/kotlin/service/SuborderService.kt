package com.buoyancy.restaurant.service

import com.buoyancy.common.model.entity.Suborder
import java.util.UUID

interface SuborderService {

    fun markSuborderAsPreparing(id: UUID)
    fun markSuborderAsPreparing(suborder: Suborder)
    fun markSuborderAsReady(id: UUID)
    fun markSuborderAsReady(suborder: Suborder)
    fun postponeSuborder(id: UUID)
    fun postponeSuborder(suborder: Suborder)
}