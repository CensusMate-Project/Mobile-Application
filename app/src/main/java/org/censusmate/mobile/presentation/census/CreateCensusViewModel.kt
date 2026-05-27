package org.censusmate.mobile.presentation.census

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.censusmate.mobile.data.remote.model.CreatePersonRequestDto
import org.censusmate.mobile.domain.usecase.household.CreateHouseholdUseCase
import org.censusmate.mobile.domain.usecase.person.CreatePersonUseCase

class CreateCensusViewModel(
    private val createHouseholdUseCase: CreateHouseholdUseCase,
    private val createPersonUseCase: CreatePersonUseCase
) : ViewModel() {
    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    var state by mutableStateOf<State>(State.Idle)
        private set

    var persons by mutableStateOf<List<PersonForm>>(emptyList())
        private set

    fun addPerson(person: PersonForm) {
        persons = persons + person
    }

    fun removePerson(index: Int) {
        persons = persons.toMutableList().also { it.removeAt(index) }
    }

    fun submit(
        address: String,
        totalResidents: Int,
        dwellingType: String?,
        buildingYear: String?,
        totalArea: Int?,
        livingArea: Int?,
        roomsCount: Int?,
        notes: String?
    ) {
        if (address.isBlank()) {
            state = State.Error("Введите адрес")
            return
        }
        if (totalResidents < 1) {
            state = State.Error("Укажите количество жителей")
            return
        }

        viewModelScope.launch {
            state = State.Loading

            val householdResult = createHouseholdUseCase(
                address,
                totalResidents,
                dwellingType,
                buildingYear,
                totalArea,
                livingArea,
                roomsCount,
                notes
            )

            householdResult.onFailure {
                state = State.Error(it.message ?: "Ошибка создания домохозяйства")
                return@launch
            }

            val household = householdResult.getOrNull() ?: return@launch

            persons.forEach { form ->
                createPersonUseCase(
                    householdId = household.id, dto = form.toRequestDto()
                )
            }

            state = State.Success
        }
    }

    companion object {
        fun factory(
            createHouseholdUseCase: CreateHouseholdUseCase,
            createPersonUseCase: CreatePersonUseCase
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CreateCensusViewModel(createHouseholdUseCase, createPersonUseCase) as T
            }
        }
    }
}

data class PersonForm(
    val gender: String? = null,
    val birthDate: String = "",
    val citizenship: String? = null,
    val hasDualCitizenship: Boolean? = null,
    val nationality: String? = null,
    val nativeLanguage: String? = null,
    val speaksRussian: Boolean? = null,
    val otherLanguages: List<String> = emptyList(),
    val educationLevel: String? = null,
    val maritalStatus: String? = null,
    val childrenCount: Int? = null,
    val relationToHousehold: String? = null,
    val placeOfBirth: String? = null,
    val currentResidence: String? = null,
    val employmentStatus: String? = null,
    val incomeSources: List<String> = emptyList()
) {
    val displayName: String
        get() = buildString {
            append(gender?.let { if (it == "male") "Мужчина" else "Женщина" } ?: "Житель")
            if (birthDate.isNotBlank()) append(", $birthDate")
            relationToHousehold?.let { append(", $it") }
        }

    fun toRequestDto() = CreatePersonRequestDto(
        gender = gender,
        birthDate = birthDate,
        citizenship = citizenship,
        hasDualCitizenship = hasDualCitizenship,
        nationality = nationality,
        nativeLanguage = nativeLanguage,
        speaksRussian = speaksRussian,
        otherLanguages = otherLanguages,
        educationLevel = educationLevel,
        maritalStatus = maritalStatus,
        childrenCount = childrenCount,
        relationToHousehold = relationToHousehold,
        placeOfBirth = placeOfBirth,
        currentResidence = currentResidence,
        incomeSources = incomeSources,
        employmentStatus = employmentStatus
    )
}