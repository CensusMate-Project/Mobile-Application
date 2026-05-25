package org.censusmate.mobile.presentation.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.usecase.user.CreateUserUseCase
import kotlin.onFailure

class CreateUserViewModel(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {
    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Idle)
        private set

    fun createUser(
        email: String, password: String, firstName: String, lastName: String, role: String
    ) {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            state = State.Error("Заполните все обязательные поля")
            return
        }

        viewModelScope.launch {
            state = State.Loading
            createUserUseCase(email, password, firstName, lastName, role).onSuccess {
                    state = State.Success
                }.onFailure { state = State.Error(it.message ?: "Ошибка создания") }
        }
    }

    companion object {
        fun factory(createUserUseCase: CreateUserUseCase) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CreateUserViewModel(createUserUseCase) as T
                }
            }
    }
}