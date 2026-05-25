package org.censusmate.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.censusmate.mobile.di.AppContainer
import org.censusmate.mobile.presentation.home.HomeRoute
import org.censusmate.mobile.presentation.login.LoginRoute
import org.censusmate.mobile.presentation.settings.SettingsRoute
import org.censusmate.mobile.presentation.users.CreateUserRoute
import org.censusmate.mobile.presentation.users.UpdateUserRoute
import org.censusmate.mobile.presentation.users.UsersRoute

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Users : Screen("users")
    object CreateUser : Screen("users/create")
    object UpdateUser : Screen("users/{userId}") {
        fun createRoute(userId: String) = "users/$userId"
    }

    object Events : Screen("events")
    object Households : Screen("households")
    object Stats : Screen("stats")
}

@Composable
fun AppNavGraph(
    navController: NavHostController, startDestination: String, container: AppContainer
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginRoute(
                loginUseCase = container.loginUseCase,
                getMeUseCase = container.getMeUseCase,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                })
        }

        composable(Screen.Home.route) {
            HomeRoute(
                getMeUseCase = container.getMeUseCase,
                logoutUseCase = container.logoutUseCase,
                onNavigateToUsers = { navController.navigate(Screen.Users.route) },
                onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                onNavigateToHouseholds = { navController.navigate(Screen.Households.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
            )
        }

        composable(Screen.Users.route) { backStackEntry ->
            val shouldRefresh by backStackEntry.savedStateHandle.getStateFlow(
                "users_refresh",
                false
            ).collectAsState()

            UsersRoute(
                getUsersUseCase = container.getUsersUseCase,
                blockUserUseCase = container.blockUserUseCase,
                shouldRefresh = shouldRefresh,
                onRefreshHandled = {
                    backStackEntry.savedStateHandle["users_refresh"] = false
                },
                onNavigateToCreate = { navController.navigate(Screen.CreateUser.route) },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.UpdateUser.createRoute(id))
                },
                onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.UpdateUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UpdateUserRoute(
                userId = userId,
                getUserUseCase = container.getUserUseCase,
                updateUserUseCase = container.updateUserUseCase,
                onBack = { navController.popBackStack() },
                onUpdated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "users_refresh",
                        true
                    )
                    navController.popBackStack()
                })
        }

        composable(Screen.CreateUser.route) {
            CreateUserRoute(
                createUserUseCase = container.createUserUseCase,
                onBack = { navController.popBackStack() },
                onCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "users_refresh",
                        true
                    )
                    navController.popBackStack()
                })
        }

        composable(Screen.Settings.route) {
            SettingsRoute(
                getMeUseCase = container.getMeUseCase,
                logoutUseCase = container.logoutUseCase,
                themeDataStore = container.themeDataStore,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                })
        }
    }
}