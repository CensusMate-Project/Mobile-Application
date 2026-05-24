package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.Stats
import org.censusmate.mobile.domain.repository.StatsRepository

class StatsRepositoryImpl(private val api: CensusApi) : StatsRepository {
    override suspend fun getEventStats(eventId: String): Result<Stats> = runCatching {
        api.stats.apiStatsIdGet(eventId).body().toDomain()
    }
}