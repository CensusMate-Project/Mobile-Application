package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.repository.PersonRepository
import org.censusmate.mobile.data.remote.model.CreatePersonRequestDto
import org.censusmate.mobile.data.remote.model.UpdatePersonRequestDto

class PersonRepositoryImpl(private val api: CensusApi) : PersonRepository {
    override suspend fun getPersons(
        householdId: String,
        page: Int,
        limit: Int
    ): Result<Paged<Person>> = runCatching {
        api.persons.apiHouseholdsHouseholdIdPersonsGet(householdId, page, limit).body().toDomain()
    }

    override suspend fun getPerson(id: String): Result<Person> = runCatching {
        api.persons.apiPersonsPersonIdGet(id).body().toDomain()
    }

    override suspend fun createPerson(
        householdId: String,
        dto: CreatePersonRequestDto
    ): Result<Person> = runCatching {
        api.persons.apiHouseholdsHouseholdIdPersonsPost(householdId, dto)
            .body().toDomain()
    }

    override suspend fun updatePerson(
        id: String,
        dto: UpdatePersonRequestDto
    ): Result<Person> = runCatching {
        api.persons.apiPersonsPersonIdPut(id, dto)
            .body().toDomain()
    }

    override suspend fun deletePerson(id: String): Result<Unit> = runCatching {
        api.persons.apiPersonsPersonIdDelete(id)
    }
}