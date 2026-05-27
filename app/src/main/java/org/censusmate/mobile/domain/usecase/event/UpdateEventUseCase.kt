package org.censusmate.mobile.domain.usecase.event

import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.repository.EventRepository

class UpdateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
        id: String,
        name: String? = null,
        startDatetime: String? = null,
        endDatetime: String? = null
    ): Result<Event> =
        repository.updateEvent(id, name, startDatetime, endDatetime)
}