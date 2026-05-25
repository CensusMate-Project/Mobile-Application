package org.censusmate.mobile.presentation.census

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.usecase.household.GetHouseholdUseCase
import org.censusmate.mobile.domain.usecase.person.CreatePersonUseCase
import org.censusmate.mobile.domain.usecase.person.DeletePersonUseCase
import org.censusmate.mobile.domain.usecase.person.GetPersonsUseCase

class CensusDetailViewModel(
    private val getHouseholdUseCase: GetHouseholdUseCase,
    private val getPersonsUseCase: GetPersonsUseCase,
    private val deletePersonUseCase: DeletePersonUseCase,
    private val createPersonUseCase: CreatePersonUseCase
) : ViewModel() {
    sealed class State {
        object Loading : State()
        data class Success(
            val household: Household,
            val persons: List<Person>
        ) : State()

        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Loading)
        private set

    fun load(householdId: String) {
        viewModelScope.launch {
            state = State.Loading
            getHouseholdUseCase(householdId).onSuccess { household ->
                    getPersonsUseCase(householdId).onSuccess { paged ->
                            state = State.Success(
                                household = household, persons = paged.items
                            )
                        }.onFailure { state = State.Error(it.message ?: "Ошибка загрузки жителей") }
                }.onFailure { state = State.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun deletePerson(personId: String, householdId: String) {
        viewModelScope.launch {
            deletePersonUseCase(personId).onSuccess { load(householdId) }
        }
    }

    fun addPerson(householdId: String, form: PersonForm) {
        viewModelScope.launch {
            createPersonUseCase(householdId, form.toRequestDto()).onSuccess { load(householdId) }
                .onFailure {
                    val current = state as? State.Success ?: return@onFailure
                    state = State.Error(it.message ?: "Ошибка добавления жителя")
                    state = current
                }
        }
    }

    companion object {
        fun factory(
            getHouseholdUseCase: GetHouseholdUseCase,
            getPersonsUseCase: GetPersonsUseCase,
            deletePersonUseCase: DeletePersonUseCase,
            createPersonUseCase: CreatePersonUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CensusDetailViewModel(
                    getHouseholdUseCase,
                    getPersonsUseCase,
                    deletePersonUseCase,
                    createPersonUseCase
                ) as T
            }
        }
    }
}