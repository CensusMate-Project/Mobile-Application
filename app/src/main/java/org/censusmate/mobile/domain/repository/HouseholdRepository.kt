package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.model.Paged

interface HouseholdRepository {
    suspend fun getHouseholds(page: Int = 1, limit: Int = 10): Result<Paged<Household>>
    suspend fun getHousehold(id: String): Result<Household>
    suspend fun createHousehold(
        address: String,
        totalResidents: Int,
        dwellingType: String? = null,
        buildingYear: String? = null,
        totalArea: Int? = null,
        livingArea: Int? = null,
        roomsCount: Int? = null,
        notes: String? = null
    ): Result<Household>

    suspend fun updateHousehold(
        id: String,
        address: String? = null,
        totalResidents: Int? = null,
        dwellingType: String? = null,
        buildingYear: String? = null,
        totalArea: Int? = null,
        livingArea: Int? = null,
        roomsCount: Int? = null,
        notes: String? = null
    ): Result<Household>

    suspend fun deleteHousehold(id: String): Result<Unit>
}