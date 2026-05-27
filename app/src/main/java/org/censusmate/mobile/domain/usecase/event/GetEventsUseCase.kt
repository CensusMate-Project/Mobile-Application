package org.censusmate.mobile.domain.usecase.event

import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.repository.EventRepository

class GetEventsUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Result<Paged<Event>> =
        repository.getEvents(page, limit)
}