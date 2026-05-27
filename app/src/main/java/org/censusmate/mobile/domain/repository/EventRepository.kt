package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.model.Paged

interface EventRepository {
    suspend fun getEvents(page: Int = 1, limit: Int = 10): Result<Paged<Event>>
    suspend fun getEvent(id: String): Result<Event>
    suspend fun createEvent(
        name: String,
        startDatetime: String,
        endDatetime: String
    ): Result<Event>

    suspend fun updateEvent(
        id: String,
        name: String? = null,
        startDatetime: String? = null,
        endDatetime: String? = null
    ): Result<Event>

    suspend fun deleteEvent(id: String): Result<Unit>
}