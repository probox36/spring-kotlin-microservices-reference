package com.buoyancy.restaurant.service

import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.entity.Suborder
import java.util.*

interface SuborderService {

    fun markSuborderAsPreparing(id: UUID): SuborderDto
    fun markSuborderAsReady(id: UUID): SuborderDto
    fun postponeSuborder(id: UUID): SuborderDto
    fun getSuborder(id: UUID): SuborderDto
    fun getSuborderEntity(id: UUID): Suborder
}