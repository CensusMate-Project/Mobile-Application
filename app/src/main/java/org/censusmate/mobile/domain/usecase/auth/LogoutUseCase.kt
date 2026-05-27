package org.censusmate.mobile.domain.usecase.auth

import org.censusmate.mobile.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}