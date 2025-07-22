package com.buoyancy.restaurant.controller

import com.buoyancy.common.model.dto.SuborderDto
import com.buoyancy.common.model.mapper.SuborderMapper
import com.buoyancy.restaurant.repository.SuborderRepository
import com.buoyancy.restaurant.service.SuborderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/suborders")
class SuborderController {

    @Autowired
    lateinit var repo: SuborderRepository
    @Autowired
    lateinit var mapper: SuborderMapper
    @Autowired
    lateinit var service: SuborderService

    @GetMapping()
    fun getSuborders(pageable: Pageable): Page<SuborderDto> {
        return repo.findAll(pageable).map { mapper.toDto(it) }
    }

    @PostMapping("/{id}/accept")
    fun acceptOrder(@PathVariable id: UUID) {
        service.markSuborderAsPreparing(id)
    }

    @PostMapping("/{id}/postpone")
    fun postponeOrderPreparation(@PathVariable id: UUID, @RequestParam("reason") reason: String) {
        service.postponeSuborder(id)
    }

    @PostMapping("/{id}/finish")
    fun finishOrderPreparation(@PathVariable id: UUID) {
        service.markSuborderAsReady(id)
    }

}