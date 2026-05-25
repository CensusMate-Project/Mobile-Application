package org.censusmate.mobile.domain.usecase.address

import org.censusmate.mobile.domain.model.Address
import org.censusmate.mobile.domain.repository.AddressRepository

class SuggestAddressUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(
        query: String,
        count: Int = 5
    ): Result<List<Address>> =
        repository.suggest(query, count)
}