package org.censusmate.mobile.domain.usecase.person

import org.censusmate.mobile.domain.repository.PersonRepository

class DeletePersonUseCase(private val repository: PersonRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.deletePerson(id)
}