package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.Address
import org.censusmate.mobile.domain.repository.AddressRepository

class AddressRepositoryImpl(private val api: CensusApi) : AddressRepository {
    override suspend fun suggest(query: String, count: Int): Result<List<Address>> =
        runCatching {
            api.address.apiAddressSuggestGet(query, count).body()
                .suggestions
                .map { it.toDomain() }
        }
}