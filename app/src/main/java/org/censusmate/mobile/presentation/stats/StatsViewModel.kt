package org.censusmate.mobile.presentation.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.model.Stats
import org.censusmate.mobile.domain.usecase.event.GetEventsUseCase
import org.censusmate.mobile.domain.usecase.stats.GetStatsUseCase

class StatsViewModel(
    private val getEventsUseCase: GetEventsUseCase,
    private val getStatsUseCase: GetStatsUseCase
) : ViewModel() {
    sealed class State {
        object Idle : State()
        object LoadingEvents : State()
        data class LoadingStats(
            val events: List<Event>
        ) : State()
        data class EventsLoaded(val events: List<Event>) : State()
        data class Success(val stats: Stats, val events: List<Event>) : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Idle)
        private set
    var selectedEvent by mutableStateOf<Event?>(null)
        private set

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            state = State.LoadingEvents
            getEventsUseCase(page = 1, limit = 100).onSuccess { paged ->
                    state = State.EventsLoaded(paged.items)
                    paged.items.firstOrNull { it.isActive }?.let { selectEvent(it) }
                }.onFailure { state = State.Error(it.message ?: "Ошибка загрузки событий") }
        }
    }

    fun selectEvent(event: Event) {
        selectedEvent = event

        val events =
            (state as? State.EventsLoaded)?.events
                ?: (state as? State.Success)?.events
                ?: (state as? State.LoadingStats)?.events
                ?: return

        viewModelScope.launch {
            state = State.LoadingStats(events)

            getStatsUseCase(event.id).onSuccess {
                state = State.Success(it, events)
            }.onFailure {
                state = State.Error(it.message ?: "Ошибка загрузки статистики")
            }
        }
    }

    companion object {
        fun factory(
            getEventsUseCase: GetEventsUseCase,
            getStatsUseCase: GetStatsUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return StatsViewModel(getEventsUseCase, getStatsUseCase) as T
            }
        }
    }
}