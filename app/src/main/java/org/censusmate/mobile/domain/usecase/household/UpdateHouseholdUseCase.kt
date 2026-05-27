package org.censusmate.mobile.domain.usecase.household

import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.repository.HouseholdRepository

class UpdateHouseholdUseCase(private val repository: HouseholdRepository) {
    suspend operator fun invoke(
        id: String,
        address: String? = null,
        totalResidents: Int? = null,
        dwellingType: String? = null,
        buildingYear: String? = null,
        totalArea: Int? = null,
        livingArea: Int? = null,
        roomsCount: Int? = null,
        notes: String? = null
    ): Result<Household> =
        repository.updateHousehold(
            id, address, totalResidents, dwellingType,
            buildingYear, totalArea, livingArea, roomsCount, notes
        )
}