package org.censusmate.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.censusmate.mobile.data.local.Theme
import org.censusmate.mobile.di.AppContainer
import org.censusmate.mobile.navigation.AppNavGraph
import org.censusmate.mobile.navigation.Screen
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

class MainActivity : ComponentActivity() {
    private val container by lazy { AppContainer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = runBlocking {
            if (container.tokenDataStore.get() != null) Screen.Home.route
            else Screen.Login.route
        }

        setContent {
            val theme by container.themeDataStore.themeFlow
                .collectAsState(initial = Theme.SYSTEM)

            val darkTheme = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            MobileApplicationTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                val token by container.tokenDataStore.tokenFlow
                    .collectAsState(initial = runBlocking { container.tokenDataStore.get() })

                LaunchedEffect(token) {
                    if (token == null &&
                        navController.currentDestination?.route != Screen.Login.route
                    ) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    container = container
                )
            }
        }
    }
}