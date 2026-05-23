package org.censusmate.mobile.domain.repository

import org.censusmate.mobile.domain.model.AuthUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun getMe(): Result<AuthUser>
}