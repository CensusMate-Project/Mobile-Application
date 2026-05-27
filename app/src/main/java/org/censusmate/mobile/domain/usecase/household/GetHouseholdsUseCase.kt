package org.censusmate.mobile.domain.usecase.household

import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.repository.HouseholdRepository

class GetHouseholdsUseCase(private val repository: HouseholdRepository) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Result<Paged<Household>> =
        repository.getHouseholds(page, limit)
}