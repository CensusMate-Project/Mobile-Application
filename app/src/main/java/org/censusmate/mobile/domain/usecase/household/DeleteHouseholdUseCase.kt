package org.censusmate.mobile.domain.usecase.household

import org.censusmate.mobile.domain.repository.HouseholdRepository

class DeleteHouseholdUseCase(private val repository: HouseholdRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.deleteHousehold(id)
}