package org.censusmate.mobile.data.repository

import org.censusmate.mobile.data.mapper.toDomain
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.repository.UserRepository
import org.censusmate.mobile.data.remote.model.BlockUserRequestDto
import org.censusmate.mobile.data.remote.model.CreateUserRequestDto
import org.censusmate.mobile.data.remote.model.UpdateUserRequestDto

class UserRepositoryImpl(private val api: CensusApi) : UserRepository {
    override suspend fun getUsers(page: Int, limit: Int): Result<Paged<User>> = runCatching {
        api.users.apiUsersGet(page, limit).body().toDomain()
    }

    override suspend fun getUser(id: String): Result<User> = runCatching {
        api.users.apiUsersIdGet(id).body().toDomain()
    }

    override suspend fun createUser(
        email: String, password: String, firstName: String, lastName: String, role: String
    ): Result<User> = runCatching {
        api.users.apiUsersPost(
            CreateUserRequestDto(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                role = role
            )
        ).body().toDomain()
    }

    override suspend fun updateUser(
        id: String,
        email: String?,
        firstName: String?,
        lastName: String?,
        newPassword: String?
    ): Result<User> = runCatching {
        api.users.apiUsersIdPut(
            id, UpdateUserRequestDto(
                email = email,
                firstName = firstName,
                lastName = lastName,
                newPassword = newPassword
            )
        ).body().toDomain()
    }

    override suspend fun blockUser(id: String, isBlocked: Boolean): Result<User> = runCatching {
        api.users.apiUsersIdBlockPost(id, BlockUserRequestDto(isBlocked = isBlocked)).body()
            .toDomain()
    }
}