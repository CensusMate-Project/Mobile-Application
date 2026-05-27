package org.censusmate.mobile.domain.usecase.user

import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(
        id: String,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        newPassword: String? = null
    ): Result<User> =
        repository.updateUser(
            id,
            email,
            firstName,
            lastName,
            newPassword,
        )
}