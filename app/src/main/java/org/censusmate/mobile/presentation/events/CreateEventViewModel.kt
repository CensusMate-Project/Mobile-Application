package org.censusmate.mobile.presentation.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.usecase.event.CreateEventUseCase

class CreateEventViewModel(
    private val createEventUseCase: CreateEventUseCase
) : ViewModel() {
    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Idle)
        private set

    fun createEvent(name: String, startDatetime: String, endDatetime: String) {
        if (name.isBlank() || startDatetime.isBlank() || endDatetime.isBlank()) {
            state = State.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            state = State.Loading
            createEventUseCase(name, startDatetime, endDatetime).onSuccess { state = State.Success }
                .onFailure { state = State.Error(it.message ?: "Ошибка создания") }
        }
    }

    companion object {
        fun factory(createEventUseCase: CreateEventUseCase) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CreateEventViewModel(createEventUseCase) as T
                }
            }
    }
}