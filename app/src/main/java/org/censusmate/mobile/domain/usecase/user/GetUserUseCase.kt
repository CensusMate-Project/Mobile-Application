package org.censusmate.mobile.domain.usecase.user

import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.repository.UserRepository

class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: String): Result<User> =
        repository.getUser(id)
}