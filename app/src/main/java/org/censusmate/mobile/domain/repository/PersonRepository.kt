package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.Person
import org.censusmate.model.CreatePersonRequestDto
import org.censusmate.model.UpdatePersonRequestDto

interface PersonRepository {
    suspend fun getPersons(
        householdId: String,
        page: Int = 1,
        limit: Int = 10
    ): Result<Paged<Person>>

    suspend fun getPerson(id: String): Result<Person>
    suspend fun createPerson(householdId: String, dto: CreatePersonRequestDto): Result<Person>
    suspend fun updatePerson(id: String, dto: UpdatePersonRequestDto): Result<Person>
    suspend fun deletePerson(id: String): Result<Unit>
}