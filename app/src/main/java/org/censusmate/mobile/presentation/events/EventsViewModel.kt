package org.censusmate.mobile.presentation.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.event.DeleteEventUseCase
import org.censusmate.mobile.domain.usecase.event.GetEventsUseCase

class EventsViewModel(
    private val getMeUseCase: GetMeUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(
            val events: List<Event>,
            val isAdmin: Boolean,
            val isLastPage: Boolean
        ) : State()

        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    private var isAdmin = false
    private var currentPage = 1
    private val limit = 20

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            state = State.Loading
            getMeUseCase()
                .onSuccess { user ->
                    isAdmin = user.isAdmin
                    loadEvents()
                }
                .onFailure { state = State.Error(it.message ?: "Ошибка") }
        }
    }

    fun loadEvents(refresh: Boolean = false) {
        if (refresh) currentPage = 1
        viewModelScope.launch {
            if (refresh) state = State.Loading
            getEventsUseCase(page = currentPage, limit = limit)
                .onSuccess { paged ->
                    val existing = if (refresh) emptyList()
                    else (state as? State.Success)?.events ?: emptyList()

                    val filtered = if (isAdmin) paged.items
                    else paged.items.filter { it.isActive }

                    state = State.Success(
                        events = existing + filtered,
                        isAdmin = isAdmin,
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
        loadEvents()
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            deleteEventUseCase(id)
                .onSuccess { loadEvents(refresh = true) }
                .onFailure {
                    val current = state as? State.Success ?: return@onFailure
                    state = State.Error(it.message ?: "Ошибка удаления")
                    state = current
                }
        }
    }

    companion object {
        fun factory(
            getMeUseCase: GetMeUseCase,
            getEventsUseCase: GetEventsUseCase,
            deleteEventUseCase: DeleteEventUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EventsViewModel(getMeUseCase, getEventsUseCase, deleteEventUseCase) as T
            }
        }
    }
}