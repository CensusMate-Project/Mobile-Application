package org.censusmate.mobile.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getMeUseCase: GetMeUseCase
) : ViewModel() {
    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Success(val user: AuthUser) : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Idle)
        private set

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            state = State.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            state = State.Loading
            loginUseCase(email, password)
                .onSuccess {
                    getMeUseCase()
                        .onSuccess { user -> state = State.Success(user) }
                        .onFailure { state = State.Error("Не удалось получить профиль") }
                }
                .onFailure { error ->
                    state = State.Error(
                        when (error) {
                            is io.ktor.client.plugins.ClientRequestException -> {
                                when (error.response.status.value) {
                                    401 -> "Неверный email или пароль"
                                    403 -> "Аккаунт заблокирован"
                                    else -> "Ошибка сервера: ${error.response.status.value}"
                                }
                            }

                            is io.ktor.client.plugins.ServerResponseException ->
                                "Сервер недоступен. Попробуйте позже"

                            is java.net.ConnectException,
                            is java.net.UnknownHostException ->
                                "Нет подключения к серверу. Проверьте сеть"

                            is java.net.SocketTimeoutException ->
                                "Превышено время ожидания. Попробуйте снова"

                            else -> error.message ?: "Неизвестная ошибка"
                        }
                    )
                }
        }
    }

    companion object {
        fun factory(
            loginUseCase: LoginUseCase,
            getMeUseCase: GetMeUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(loginUseCase, getMeUseCase) as T
            }
        }
    }
}
