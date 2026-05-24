package org.censusmate.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.censusmate.mobile.data.local.Theme
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
import org.censusmate.mobile.navigation.AppNavGraph
import org.censusmate.mobile.navigation.Screen
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

class MainActivity : ComponentActivity() {
    private val tokenDataStore by lazy { TokenDataStore(this) }
    private val themeDataStore by lazy { ThemeDataStore(this) }

    private val censusApi by lazy { CensusApi(tokenDataStore) }

    private val authRepository by lazy { AuthRepositoryImpl(censusApi, tokenDataStore) }
    private val userRepository by lazy { UserRepositoryImpl(censusApi) }
    private val eventRepository by lazy { EventRepositoryImpl(censusApi) }
    private val householdRepository by lazy { HouseholdRepositoryImpl(censusApi) }
    private val personRepository by lazy { PersonRepositoryImpl(censusApi) }
    private val statsRepository by lazy { StatsRepositoryImpl(censusApi) }
    private val addressRepository by lazy { AddressRepositoryImpl(censusApi) }

    private val loginUseCase by lazy { LoginUseCase(authRepository) }
    private val logoutUseCase by lazy { LogoutUseCase(authRepository) }
    private val getMeUseCase by lazy { GetMeUseCase(authRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = runBlocking {
            if (tokenDataStore.get() != null) Screen.Home.route
            else Screen.Login.route
        }

        setContent {
            val theme by themeDataStore.themeFlow
                .collectAsState(initial = Theme.SYSTEM)

            val darkTheme = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            MobileApplicationTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    loginUseCase = loginUseCase,
                    logoutUseCase = logoutUseCase,
                    getMeUseCase = getMeUseCase,
                    themeDataStore = themeDataStore
                )
            }
        }
    }
}