package org.censusmate.mobile.domain.usecase.person

import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.repository.PersonRepository

class GetPersonUseCase(private val repository: PersonRepository) {
    suspend operator fun invoke(id: String): Result<Person> =
        repository.getPerson(id)
}