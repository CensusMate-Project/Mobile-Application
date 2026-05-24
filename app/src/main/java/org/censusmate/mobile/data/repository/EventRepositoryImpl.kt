package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.repository.EventRepository
import org.censusmate.model.CreateEventRequestDto
import org.censusmate.model.UpdateEventRequestDto

class EventRepositoryImpl(private val api: CensusApi) : EventRepository {
    override suspend fun getEvents(page: Int, limit: Int): Result<Paged<Event>> =
        runCatching {
            api.events.apiEventsGet(page, limit).body().toDomain()
        }

    override suspend fun getEvent(id: String): Result<Event> = runCatching {
        api.events.apiEventsIdGet(id).body().toDomain()
    }

    override suspend fun createEvent(
        name: String,
        startDatetime: String,
        endDatetime: String
    ): Result<Event> = runCatching {
        api.events.apiEventsPost(
            CreateEventRequestDto(
                name = name,
                startDatetime = startDatetime,
                endDatetime = endDatetime
            )
        ).body().toDomain()
    }

    override suspend fun updateEvent(
        id: String,
        name: String?,
        startDatetime: String?,
        endDatetime: String?
    ): Result<Event> = runCatching {
        api.events.apiEventsIdPut(
            id,
            UpdateEventRequestDto(
                name = name,
                startDatetime = startDatetime,
                endDatetime = endDatetime
            )
        ).body().toDomain()
    }

    override suspend fun deleteEvent(id: String): Result<Unit> = runCatching {
        api.events.apiEventsIdDelete(id)
    }
}