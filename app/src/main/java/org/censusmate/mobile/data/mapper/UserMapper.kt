package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.Role
import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.data.remote.model.UserResponseDto
import org.censusmate.mobile.data.remote.model.UsersResponseDto

fun UserResponseDto.toDomain() = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = Role.fromString(role),
    isBlocked = isBlocked,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UsersResponseDto.toDomain(): Paged<User> = Paged(
    total = pagination.total,
    page = pagination.page,
    limit = pagination.limit,
    pages = pagination.pages,
    items = users.map { it.toDomain() }
)