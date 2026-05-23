package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.model.Role
import org.censusmate.model.MeResponseDto



fun MeResponseDto.toDomain() = AuthUser(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = Role.fromString(role)
)