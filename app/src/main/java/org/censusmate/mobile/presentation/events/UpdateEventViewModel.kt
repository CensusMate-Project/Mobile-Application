package org.censusmate.mobile.presentation.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.usecase.event.GetEventUseCase
import org.censusmate.mobile.domain.usecase.event.UpdateEventUseCase
import kotlin.onFailure

class UpdateEventViewModel(
    private val getEventUseCase: GetEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(val event: Event) : State()
        object Updated : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    fun loadEvent(id: String) {
        viewModelScope.launch {
            state = State.Loading
            getEventUseCase(id).onSuccess { state = State.Success(it) }
                .onFailure { state = State.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun updateEvent(id: String, name: String, startDatetime: String, endDatetime: String) {
        if (name.isBlank() || startDatetime.isBlank() || endDatetime.isBlank()) {
            state = State.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            state = State.Loading
            updateEventUseCase(id, name, startDatetime, endDatetime).onSuccess {
                    state = State.Updated
                }.onFailure { state = State.Error(it.message ?: "Ошибка обновления") }
        }
    }

    companion object {
        fun factory(
            getEventUseCase: GetEventUseCase,
            updateEventUseCase: UpdateEventUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UpdateEventViewModel(getEventUseCase, updateEventUseCase) as T
            }
        }
    }
}