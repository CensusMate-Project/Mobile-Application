package org.censusmate.mobile.domain.usecase.user

import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.repository.UserRepository

class CreateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: String
    ): Result<User> =
        repository.createUser(
            email,
            password,
            firstName,
            lastName,
            role,
        )
}