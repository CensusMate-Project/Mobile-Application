package org.censusmate.mobile.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LogoutUseCase

class HomeViewModel(
    private val getMeUseCase: GetMeUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(val user: AuthUser) : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            state = State.Loading
            getMeUseCase()
                .onSuccess { state = State.Success(it) }
                .onFailure { state = State.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLoggedOut()
        }
    }

    companion object {
        fun factory(
            getMeUseCase: GetMeUseCase,
            logoutUseCase: LogoutUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(getMeUseCase, logoutUseCase) as T
            }
        }
    }
}