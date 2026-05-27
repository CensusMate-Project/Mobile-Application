package org.censusmate.mobile.presentation.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.User
import org.censusmate.mobile.domain.usecase.user.BlockUserUseCase
import org.censusmate.mobile.domain.usecase.user.GetUsersUseCase

class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val blockUserUseCase: BlockUserUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(val users: List<User>, val isLastPage: Boolean) : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    private var currentPage = 1
    private val limit = 20

    init {
        loadUsers()
    }

    fun loadUsers(refresh: Boolean = false) {
        if (refresh) currentPage = 1
        viewModelScope.launch {
            state = State.Loading
            getUsersUseCase(page = currentPage, limit = limit)
                .onSuccess { paged ->
                    state = State.Success(
                        users = paged.items,
                        isLastPage = currentPage >= paged.pages
                    )
                }
                .onFailure { state = State.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun loadNextPage() {
        val current = state as? State.Success ?: return
        if (current.isLastPage) return
        currentPage++
        viewModelScope.launch {
            getUsersUseCase(page = currentPage, limit = limit)
                .onSuccess { paged ->
                    state = State.Success(
                        users = current.users + paged.items,
                        isLastPage = currentPage >= paged.pages
                    )
                }
                .onFailure { currentPage-- }
        }
    }

    fun blockUser(id: String, isBlocked: Boolean) {
        viewModelScope.launch {
            blockUserUseCase(id, isBlocked)
                .onSuccess { loadUsers(refresh = true) }
        }
    }

    companion object {
        fun factory(
            getUsersUseCase: GetUsersUseCase,
            blockUserUseCase: BlockUserUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UsersViewModel(getUsersUseCase, blockUserUseCase) as T
            }
        }
    }
}