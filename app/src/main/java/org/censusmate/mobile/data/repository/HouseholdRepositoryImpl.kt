package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.repository.HouseholdRepository
import org.censusmate.mobile.data.remote.model.CreateHouseholdRequestDto
import org.censusmate.mobile.data.remote.model.UpdateHouseholdRequestDto

class HouseholdRepositoryImpl(private val api: CensusApi) : HouseholdRepository {
    override suspend fun getHouseholds(page: Int, limit: Int): Result<Paged<Household>> =
        runCatching {
            api.households.apiHouseholdsGet(page, limit).body().toDomain()
        }

    override suspend fun getHousehold(id: String): Result<Household> = runCatching {
        api.households.apiHouseholdsIdGet(id).body().toDomain()
    }

    override suspend fun createHousehold(
        address: String,
        totalResidents: Int,
        dwellingType: String?,
        buildingYear: String?,
        totalArea: Int?,
        livingArea: Int?,
        roomsCount: Int?,
        notes: String?
    ): Result<Household> = runCatching {
        api.households.apiHouseholdsPost(
            CreateHouseholdRequestDto(
                address = address,
                totalResidents = totalResidents,
                dwellingType = dwellingType,
                buildingYear = buildingYear,
                totalArea = totalArea,
                livingArea = livingArea,
                roomsCount = roomsCount,
                notes = notes
            )
        ).body().toDomain()
    }

    override suspend fun updateHousehold(
        id: String,
        address: String?,
        totalResidents: Int?,
        dwellingType: String?,
        buildingYear: String?,
        totalArea: Int?,
        livingArea: Int?,
        roomsCount: Int?,
        notes: String?
    ): Result<Household> = runCatching {
        api.households.apiHouseholdsIdPut(
            id,
            UpdateHouseholdRequestDto(
                address = address,
                totalResidents = totalResidents,
                dwellingType = dwellingType,
                buildingYear = buildingYear,
                totalArea = totalArea,
                livingArea = livingArea,
                roomsCount = roomsCount,
                notes = notes
            )
        ).body().toDomain()
    }

    override suspend fun deleteHousehold(id: String): Result<Unit> = runCatching {
        api.households.apiHouseholdsIdDelete(id)
    }
}