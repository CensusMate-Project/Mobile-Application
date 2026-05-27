package org.censusmate.mobile.domain.usecase.event

import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.repository.EventRepository

class CreateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
        name: String,
        startDatetime: String,
        endDatetime: String
    ): Result<Event> =
        repository.createEvent(name, startDatetime, endDatetime)
}