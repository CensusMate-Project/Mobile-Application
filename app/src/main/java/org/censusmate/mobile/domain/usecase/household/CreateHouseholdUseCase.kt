package org.censusmate.mobile.domain.usecase.household

import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.repository.HouseholdRepository

class CreateHouseholdUseCase(private val repository: HouseholdRepository) {
    suspend operator fun invoke(
        address: String,
        totalResidents: Int,
        dwellingType: String? = null,
        buildingYear: String? = null,
        totalArea: Int? = null,
        livingArea: Int? = null,
        roomsCount: Int? = null,
        notes: String? = null
    ): Result<Household> =
        repository.createHousehold(
            address, totalResidents, dwellingType,
            buildingYear, totalArea, livingArea, roomsCount, notes
        )
}