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
import org.censusmate.mobile.presentation.census.CensusDetailRoute
import org.censusmate.mobile.presentation.census.CensusListRoute
import org.censusmate.mobile.presentation.census.CreateCensusRoute
import org.censusmate.mobile.presentation.events.CreateEventRoute
import org.censusmate.mobile.presentation.events.EventsRoute
import org.censusmate.mobile.presentation.events.UpdateEventRoute
import org.censusmate.mobile.presentation.home.HomeRoute
import org.censusmate.mobile.presentation.login.LoginRoute
import org.censusmate.mobile.presentation.settings.SettingsRoute
import org.censusmate.mobile.presentation.stats.StatsRoute
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
    object CreateEvent : Screen("events/create")
    object UpdateEvent : Screen("events/{eventId}") {
        fun createRoute(id: String) = "events/$id"
    }

    object Census : Screen("census")
    object CreateCensus : Screen("census/create")
    object CensusDetail : Screen("census/{householdId}") {
        fun createRoute(id: String) = "census/$id"
    }

    object Stats : Screen("stats")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    container: AppContainer
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
                }
            )
        }

        composable(Screen.Home.route) {
            HomeRoute(
                getMeUseCase = container.getMeUseCase,
                logoutUseCase = container.logoutUseCase,
                onNavigateToUsers = { navController.navigate(Screen.Users.route) },
                onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                onNavigateToHouseholds = { navController.navigate(Screen.Census.route) },
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
                onBack = { navController.popBackStack() }
            )
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
                }
            )
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
                }
            )
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
                }
            )
        }

        composable(Screen.Events.route) { backStackEntry ->
            val shouldRefresh by backStackEntry.savedStateHandle.getStateFlow(
                    "events_refresh",
                    false
                ).collectAsState()

            EventsRoute(
                getMeUseCase = container.getMeUseCase,
                getEventsUseCase = container.getEventsUseCase,
                deleteEventUseCase = container.deleteEventUseCase,
                shouldRefresh = shouldRefresh,
                onRefreshHandled = { backStackEntry.savedStateHandle["events_refresh"] = false },
                onNavigateToCreate = { navController.navigate(Screen.CreateEvent.route) },
                onNavigateToEdit = { id -> navController.navigate(Screen.UpdateEvent.createRoute(id)) },
                onBack = { navController.popBackStack() })
        }

        composable(Screen.CreateEvent.route) {
            CreateEventRoute(
                createEventUseCase = container.createEventUseCase,
                onBack = { navController.popBackStack() },
                onCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                            "events_refresh",
                            true
                        )
                    navController.popBackStack()
                })
        }

        composable(
            route = Screen.UpdateEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            UpdateEventRoute(
                eventId = eventId,
                getEventUseCase = container.getEventUseCase,
                updateEventUseCase = container.updateEventUseCase,
                onBack = { navController.popBackStack() },
                onUpdated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                            "events_refresh",
                            true
                        )
                    navController.popBackStack()
                })
        }

        composable(Screen.Census.route) { backStackEntry ->
            val shouldRefresh by backStackEntry.savedStateHandle
                .getStateFlow("census_refresh", false)
                .collectAsState()

            CensusListRoute(
                getMeUseCase = container.getMeUseCase,
                getHouseholdsUseCase = container.getHouseholdsUseCase,
                deleteHouseholdUseCase = container.deleteHouseholdUseCase,
                shouldRefresh = shouldRefresh,
                onRefreshHandled = { backStackEntry.savedStateHandle["census_refresh"] = false },
                onNavigateToCreate = { navController.navigate(Screen.CreateCensus.route) },
                onNavigateToDetail = { id ->
                    navController.navigate(
                        Screen.CensusDetail.createRoute(
                            id
                        )
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateCensus.route) {
            CreateCensusRoute(
                createHouseholdUseCase = container.createHouseholdUseCase,
                createPersonUseCase = container.createPersonUseCase,
                suggestAddressUseCase = container.getAddressUseCase,
                onBack = { navController.popBackStack() },
                onCreated = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("census_refresh", true)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CensusDetail.route,
            arguments = listOf(navArgument("householdId") { type = NavType.StringType })
        ) { backStackEntry ->
            val householdId =
                backStackEntry.arguments?.getString("householdId") ?: return@composable
            CensusDetailRoute(
                householdId = householdId,
                getHouseholdUseCase = container.getHouseholdUseCase,
                getPersonsUseCase = container.getPersonsUseCase,
                deletePersonUseCase = container.deletePersonUseCase,
                createPersonUseCase = container.createPersonUseCase,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsRoute(
                getEventsUseCase = container.getEventsUseCase,
                getStatsUseCase = container.getStatsUseCase,
                onBack = { navController.popBackStack() }
            )
        }
    }
}