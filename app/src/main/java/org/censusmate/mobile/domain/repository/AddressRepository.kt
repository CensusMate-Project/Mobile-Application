package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.Address

interface AddressRepository {
    suspend fun suggest(query: String, count: Int = 5): Result<List<Address>>
}