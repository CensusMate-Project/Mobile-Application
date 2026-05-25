package org.censusmate.mobile.di

import android.content.Context
import org.censusmate.mobile.data.local.ThemeDataStore
import org.censusmate.mobile.data.local.TokenDataStore
import org.censusmate.mobile.data.remote.CensusApi
import org.censusmate.mobile.data.repository.AddressRepositoryImpl
import org.censusmate.mobile.data.repository.AuthRepositoryImpl
import org.censusmate.mobile.data.repository.EventRepositoryImpl
import org.censusmate.mobile.data.repository.HouseholdRepositoryImpl
import org.censusmate.mobile.data.repository.PersonRepositoryImpl
import org.censusmate.mobile.data.repository.StatsRepositoryImpl
import org.censusmate.mobile.data.repository.UserRepositoryImpl
import org.censusmate.mobile.domain.usecase.address.SuggestAddressUseCase
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LoginUseCase
import org.censusmate.mobile.domain.usecase.auth.LogoutUseCase
import org.censusmate.mobile.domain.usecase.event.CreateEventUseCase
import org.censusmate.mobile.domain.usecase.event.DeleteEventUseCase
import org.censusmate.mobile.domain.usecase.event.GetEventUseCase
import org.censusmate.mobile.domain.usecase.event.GetEventsUseCase
import org.censusmate.mobile.domain.usecase.event.UpdateEventUseCase
import org.censusmate.mobile.domain.usecase.household.CreateHouseholdUseCase
import org.censusmate.mobile.domain.usecase.household.DeleteHouseholdUseCase
import org.censusmate.mobile.domain.usecase.household.GetHouseholdUseCase
import org.censusmate.mobile.domain.usecase.household.GetHouseholdsUseCase
import org.censusmate.mobile.domain.usecase.household.UpdateHouseholdUseCase
import org.censusmate.mobile.domain.usecase.person.CreatePersonUseCase
import org.censusmate.mobile.domain.usecase.person.DeletePersonUseCase
import org.censusmate.mobile.domain.usecase.person.GetPersonUseCase
import org.censusmate.mobile.domain.usecase.person.GetPersonsUseCase
import org.censusmate.mobile.domain.usecase.person.UpdatePersonUseCase
import org.censusmate.mobile.domain.usecase.stats.GetStatsUseCase
import org.censusmate.mobile.domain.usecase.user.BlockUserUseCase
import org.censusmate.mobile.domain.usecase.user.CreateUserUseCase
import org.censusmate.mobile.domain.usecase.user.GetUserUseCase
import org.censusmate.mobile.domain.usecase.user.GetUsersUseCase
import org.censusmate.mobile.domain.usecase.user.UpdateUserUseCase

class AppContainer(context: Context) {
    val tokenDataStore = TokenDataStore(context)
    val themeDataStore = ThemeDataStore(context)

    private val censusApi = CensusApi(tokenDataStore)

    private val authRepository = AuthRepositoryImpl(censusApi, tokenDataStore)
    private val userRepository = UserRepositoryImpl(censusApi)
    private val eventRepository = EventRepositoryImpl(censusApi)
    private val householdRepository = HouseholdRepositoryImpl(censusApi)
    private val personRepository = PersonRepositoryImpl(censusApi)
    private val statsRepository = StatsRepositoryImpl(censusApi)
    private val addressRepository = AddressRepositoryImpl(censusApi)

    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getMeUseCase = GetMeUseCase(authRepository)

    val getUsersUseCase = GetUsersUseCase(userRepository)
    val getUserUseCase = GetUserUseCase(userRepository)
    val createUserUseCase = CreateUserUseCase(userRepository)
    val updateUserUseCase = UpdateUserUseCase(userRepository)
    val blockUserUseCase = BlockUserUseCase(userRepository)

    val getEventsUseCase = GetEventsUseCase(eventRepository)
    val getEventUseCase = GetEventUseCase(eventRepository)
    val createEventUseCase = CreateEventUseCase(eventRepository)
    val updateEventUseCase = UpdateEventUseCase(eventRepository)
    val deleteEventUseCase = DeleteEventUseCase(eventRepository)

    val getHouseholdsUseCase = GetHouseholdsUseCase(householdRepository)
    val getHouseholdUseCase = GetHouseholdUseCase(householdRepository)
    val createHouseholdUseCase = CreateHouseholdUseCase(householdRepository)
    val updateHouseholdUseCase = UpdateHouseholdUseCase(householdRepository)
    val deleteHouseholdUseCase = DeleteHouseholdUseCase(householdRepository)

    val getPersonsUseCase = GetPersonsUseCase(personRepository)
    val getPersonUseCase = GetPersonUseCase(personRepository)
    val createPersonUseCase = CreatePersonUseCase(personRepository)
    val updatePersonUseCase = UpdatePersonUseCase(personRepository)
    val deletePersonUseCase = DeletePersonUseCase(personRepository)

    val getStatsUseCase = GetStatsUseCase(statsRepository)
    val getAddressUseCase = SuggestAddressUseCase(addressRepository)

}