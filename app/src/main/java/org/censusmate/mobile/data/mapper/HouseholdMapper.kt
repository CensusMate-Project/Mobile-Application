package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.data.remote.model.HouseholdResponseDto
import org.censusmate.mobile.data.remote.model.HouseholdsResponseDto

fun HouseholdResponseDto.toDomain() = Household(
    id = id,
    enumeratorId = enumeratorId,
    eventId = eventId,
    address = address,
    totalResidents = totalResidents,
    dwellingType = dwellingType,
    buildingYear = buildingYear,
    totalArea = totalArea,
    livingArea = livingArea,
    roomsCount = roomsCount,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun HouseholdsResponseDto.toDomain(): Paged<Household> = Paged(
    total = pagination.total,
    page = pagination.page,
    limit = pagination.limit,
    pages = pagination.pages,
    items = households.map { it.toDomain() }
)