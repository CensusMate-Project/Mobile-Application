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
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LoginUseCase
import org.censusmate.mobile.domain.usecase.auth.LogoutUseCase
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
}