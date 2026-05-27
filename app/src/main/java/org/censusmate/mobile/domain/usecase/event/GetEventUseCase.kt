package org.censusmate.mobile.domain.usecase.event

import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.repository.EventRepository

class GetEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: String): Result<Event> =
        repository.getEvent(id)
}