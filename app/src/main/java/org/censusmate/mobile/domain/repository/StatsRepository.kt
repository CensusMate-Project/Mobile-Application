package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.Stats

interface StatsRepository {
    suspend fun getEventStats(eventId: String): Result<Stats>
}