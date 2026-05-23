package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.local.TokenDataStore
import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.repository.AuthRepository
import org.censusmate.model.LoginRequestDto

class AuthRepositoryImpl(
    private val api: CensusApi, private val tokenDataStore: TokenDataStore
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val response = api.auth.apiAuthLoginPost(
            LoginRequestDto(email = email, password = password)
        )
        tokenDataStore.saveToken(response.body().token)
    }

    override suspend fun logout() {
        tokenDataStore.clear()
    }

    override suspend fun getMe(): Result<AuthUser> = runCatching {
        api.auth.apiAuthMeGet().body().toDomain()
    }
}