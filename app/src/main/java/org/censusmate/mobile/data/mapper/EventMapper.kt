package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.data.remote.model.EventResponseDto
import org.censusmate.mobile.data.remote.model.EventsResponseDto

fun EventResponseDto.toDomain() = Event(
    id = id,
    name = name,
    startDatetime = startDatetime,
    endDatetime = endDatetime,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun EventsResponseDto.toDomain(): Paged<Event> = Paged(
    total = pagination.total,
    page = pagination.page,
    limit = pagination.limit,
    pages = pagination.pages,
    items = events.map { it.toDomain() },
)