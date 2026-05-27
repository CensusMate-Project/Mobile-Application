package org.censusmate.mobile.domain.usecase.user

import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.repository.UserRepository

class GetUsersUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Result<Paged<User>> =
        repository.getUsers(page, limit)
}