package org.censusmate.mobile.domain.usecase.person

import org.censusmate.mobile.data.remote.model.UpdatePersonRequestDto
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.repository.PersonRepository

class UpdatePersonUseCase(private val repository: PersonRepository) {
    suspend operator fun invoke(
        id: String,
        dto: UpdatePersonRequestDto
    ): Result<Person> =
        repository.updatePerson(id, dto)
}