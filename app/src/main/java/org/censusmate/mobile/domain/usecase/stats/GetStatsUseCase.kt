package org.censusmate.mobile.domain.usecase.stats

import org.censusmate.mobile.domain.model.Stats
import org.censusmate.mobile.domain.repository.StatsRepository

class GetStatsUseCase(private val repository: StatsRepository) {
    suspend operator fun invoke(eventId: String): Result<Stats> =
        repository.getEventStats(eventId)
}