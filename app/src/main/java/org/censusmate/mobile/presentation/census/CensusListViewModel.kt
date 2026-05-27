package org.censusmate.mobile.presentation.census

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.household.DeleteHouseholdUseCase
import org.censusmate.mobile.domain.usecase.household.GetHouseholdsUseCase

class CensusListViewModel(
    private val getMeUseCase: GetMeUseCase,
    private val getHouseholdsUseCase: GetHouseholdsUseCase,
    private val deleteHouseholdUseCase: DeleteHouseholdUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(
            val households: List<Household>,
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
                    loadHouseholds()
                }
                .onFailure { state = State.Error(it.message ?: "Ошибка") }
        }
    }

    fun loadHouseholds(refresh: Boolean = false) {
        if (refresh) currentPage = 1
        viewModelScope.launch {
            if (refresh) state = State.Loading
            getHouseholdsUseCase(page = currentPage, limit = limit)
                .onSuccess { paged ->
                    val existing = if (refresh) emptyList()
                    else (state as? State.Success)?.households ?: emptyList()
                    state = State.Success(
                        households = existing + paged.items,
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
        loadHouseholds()
    }

    fun deleteHousehold(id: String) {
        viewModelScope.launch {
            deleteHouseholdUseCase(id)
                .onSuccess { loadHouseholds(refresh = true) }
        }
    }

    companion object {
        fun factory(
            getMeUseCase: GetMeUseCase,
            getHouseholdsUseCase: GetHouseholdsUseCase,
            deleteHouseholdUseCase: DeleteHouseholdUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CensusListViewModel(getMeUseCase, getHouseholdsUseCase, deleteHouseholdUseCase) as T
            }
        }
    }
}