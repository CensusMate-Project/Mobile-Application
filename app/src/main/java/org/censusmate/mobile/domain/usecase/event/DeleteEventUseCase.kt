package org.censusmate.mobile.domain.usecase.event

import org.censusmate.mobile.domain.repository.EventRepository

class DeleteEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.deleteEvent(id)
}