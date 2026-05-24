package org.censusmate.mobile.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.data.local.Theme
import org.censusmate.mobile.data.local.ThemeDataStore
import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LogoutUseCase

class SettingsViewModel(
    private val getMeUseCase: GetMeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val themeDataStore: ThemeDataStore
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(val user: AuthUser, val theme: Theme) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            getMeUseCase()
                .onSuccess { user ->
                    themeDataStore.themeFlow.collect { theme ->
                        state = State.Success(user, theme)
                    }
                }
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch { themeDataStore.saveTheme(theme) }
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
            logoutUseCase: LogoutUseCase,
            themeDataStore: ThemeDataStore
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(getMeUseCase, logoutUseCase, themeDataStore) as T
            }
        }
    }
}