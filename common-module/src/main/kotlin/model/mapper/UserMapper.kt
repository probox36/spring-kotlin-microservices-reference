package com.buoyancy.common.model.mapper

import com.buoyancy.common.model.dto.UserDto
import com.buoyancy.common.model.entity.User
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface UserMapper {
    fun toDto(user: User): UserDto
    fun toEntity(dto: UserDto): User
}