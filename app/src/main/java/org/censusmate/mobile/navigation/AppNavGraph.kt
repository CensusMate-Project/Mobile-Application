package org.censusmate.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.censusmate.mobile.data.local.ThemeDataStore
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LoginUseCase
import org.censusmate.mobile.domain.usecase.auth.LogoutUseCase
import org.censusmate.mobile.presentation.home.HomeRoute
import org.censusmate.mobile.presentation.login.LoginRoute
import org.censusmate.mobile.presentation.settings.SettingsRoute

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Users : Screen("users")
    object Events : Screen("events")
    object Households : Screen("households")
    object Stats : Screen("stats")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    loginUseCase: LoginUseCase,
    logoutUseCase: LogoutUseCase,
    getMeUseCase: GetMeUseCase,
    themeDataStore: ThemeDataStore
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginRoute(
                loginUseCase = loginUseCase,
                getMeUseCase = getMeUseCase,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeRoute(
                getMeUseCase = getMeUseCase,
                logoutUseCase = logoutUseCase,
                onNavigateToUsers = { navController.navigate(Screen.Users.route) },
                onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                onNavigateToHouseholds = { navController.navigate(Screen.Households.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
            )
        }

        composable(Screen.Settings.route) {
            SettingsRoute(
                getMeUseCase = getMeUseCase,
                logoutUseCase = logoutUseCase,
                themeDataStore = themeDataStore,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}