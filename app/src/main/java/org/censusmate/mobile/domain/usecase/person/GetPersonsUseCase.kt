package org.censusmate.mobile.domain.usecase.person

import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.repository.PersonRepository

class GetPersonsUseCase(private val repository: PersonRepository) {
    suspend operator fun invoke(
        householdId: String,
        page: Int = 1,
        limit: Int = 10
    ): Result<Paged<Person>> =
        repository.getPersons(householdId, page, limit)
}