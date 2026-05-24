package org.censusmate.mobile.domain.usecase.auth

import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.repository.AuthRepository

class GetMeUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<AuthUser> = repository.getMe()
}