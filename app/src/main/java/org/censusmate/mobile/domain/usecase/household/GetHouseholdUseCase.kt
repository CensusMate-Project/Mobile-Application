package org.censusmate.mobile.domain.usecase.household

import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.repository.HouseholdRepository

class GetHouseholdUseCase(private val repository: HouseholdRepository) {
    suspend operator fun invoke(id: String): Result<Household> =
        repository.getHousehold(id)
}