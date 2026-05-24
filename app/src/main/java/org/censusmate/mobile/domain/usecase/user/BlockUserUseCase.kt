package org.censusmate.mobile.domain.usecase.user

import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.repository.UserRepository

class BlockUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: String, isBlocked: Boolean): Result<User> =
        repository.blockUser(id, isBlocked)
}