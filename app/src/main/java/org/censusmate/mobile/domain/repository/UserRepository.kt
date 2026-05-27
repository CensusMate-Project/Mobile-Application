package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.User

interface UserRepository {
    suspend fun getUsers(page: Int = 1, limit: Int = 10): Result<Paged<User>>
    suspend fun getUser(id: String): Result<User>
    suspend fun createUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: String
    ): Result<User>

    suspend fun updateUser(
        id: String,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        newPassword: String? = null
    ): Result<User>

    suspend fun blockUser(id: String, isBlocked: Boolean): Result<User>
}