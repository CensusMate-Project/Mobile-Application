package org.censusmate.mobile.presentation.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.usecase.user.GetUserUseCase
import org.censusmate.mobile.domain.usecase.user.UpdateUserUseCase

class UpdateUserViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(val user: User) : State()
        object Updated : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    fun loadUser(id: String) {
        viewModelScope.launch {
            state = State.Loading
            getUserUseCase(id).onSuccess { state = State.Success(it) }
                .onFailure { state = State.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun updateUser(
        id: String, firstName: String, lastName: String, email: String, newPassword: String?
    ) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            state = State.Error("Заполните все обязательные поля")
            return
        }
        viewModelScope.launch {
            state = State.Loading
            updateUserUseCase(
                id = id,
                firstName = firstName,
                lastName = lastName,
                email = email,
                newPassword = newPassword?.ifBlank { null }).onSuccess { state = State.Updated }
                .onFailure { state = State.Error(it.message ?: "Ошибка обновления") }
        }
    }

    companion object {
        fun factory(
            getUserUseCase: GetUserUseCase,
            updateUserUseCase: UpdateUserUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UpdateUserViewModel(getUserUseCase, updateUserUseCase) as T
            }
        }
    }
}