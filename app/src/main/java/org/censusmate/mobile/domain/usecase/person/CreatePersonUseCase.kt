package org.censusmate.mobile.domain.usecase.person

import org.censusmate.mobile.data.remote.model.CreatePersonRequestDto
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.repository.PersonRepository

class CreatePersonUseCase(private val repository: PersonRepository) {
    suspend operator fun invoke(
        householdId: String,
        dto: CreatePersonRequestDto
    ): Result<Person> =
        repository.createPerson(householdId, dto)
}