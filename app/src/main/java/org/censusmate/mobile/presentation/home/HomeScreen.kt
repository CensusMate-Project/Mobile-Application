package org.censusmate.mobile.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.R
import org.censusmate.mobile.domain.model.AuthUser
import org.censusmate.mobile.domain.model.Role
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.auth.LogoutUseCase
import org.censusmate.mobile.presentation.home.components.HomeNavCard
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun HomeRoute(
    getMeUseCase: GetMeUseCase,
    logoutUseCase: LogoutUseCase,
    onNavigateToUsers: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToHouseholds: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory(getMeUseCase, logoutUseCase)
    )
    HomeScreen(
        state = viewModel.state,
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToEvents = onNavigateToEvents,
        onNavigateToHouseholds = onNavigateToHouseholds,
        onNavigateToStats = onNavigateToStats,
        onNavigateToSettings = onNavigateToSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeViewModel.State,
    onNavigateToUsers: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToHouseholds: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.censusmate)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when (state) {
            is HomeViewModel.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is HomeViewModel.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is HomeViewModel.State.Success -> {
                val user = state.user
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User welcome card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.welcome),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(if (user.isAdmin)
                                        stringResource(R.string.administrator_role)
                                    else
                                        stringResource(R.string.agent_role))
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (user.isAdmin)
                                            Icons.Default.AdminPanelSettings
                                        else
                                            Icons.Default.Badge,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.sections),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Navigation cards
                    if (user.isAdmin) {
                        HomeNavCard(
                            title = stringResource(R.string.users),
                            subtitle = stringResource(R.string.account_management),
                            icon = Icons.Default.Group,
                            onClick = onNavigateToUsers
                        )
                        HomeNavCard(
                            title = stringResource(R.string.statistics),
                            subtitle = stringResource(R.string.census_analytics),
                            icon = Icons.Default.BarChart,
                            onClick = onNavigateToStats
                        )
                    }

                    HomeNavCard(
                        title = stringResource(R.string.census_events),
                        subtitle = if (user.isAdmin)
                            stringResource(R.string.event_management)
                        else
                            stringResource(R.string.active_events),
                        icon = Icons.Default.Event,
                        onClick = onNavigateToEvents
                    )

                    HomeNavCard(
                        title = stringResource(R.string.households),
                        subtitle = if (user.isAdmin)
                            stringResource(R.string.all_households)
                        else
                            stringResource(R.string.households_genitive),
                        icon = Icons.Default.Home,
                        onClick = onNavigateToHouseholds
                    )

                    HomeNavCard(
                        title = stringResource(R.string.settings),
                        subtitle = stringResource(R.string.theme_profile_exit),
                        icon = Icons.Default.Settings,
                        onClick = onNavigateToSettings
                    )
                }
            }
        }
    }
}

private val previewAdmin = AuthUser(
    id = "1",
    email = "admin@census.ru",
    firstName = "Иван",
    lastName = "Иванов",
    role = Role.ADMINISTRATOR
)

private val previewAgent = AuthUser(
    id = "2",
    email = "agent@census.ru",
    firstName = "Иван",
    lastName = "Петров",
    role = Role.AGENT
)

@Preview(showBackground = true, name = "Admin Home Screen")
@Composable
private fun HomeAdminPreview() {
    MobileApplicationTheme() {
        HomeScreen(
            state = HomeViewModel.State.Success(previewAdmin),
            onNavigateToUsers = {},
            onNavigateToEvents = {},
            onNavigateToHouseholds = {},
            onNavigateToStats = {},
            onNavigateToSettings = {},
        )
    }
}

@Preview(showBackground = true, name = "Agent Home Screen")
@Composable
private fun HomeAgentPreview() {
    MobileApplicationTheme() {
        HomeScreen(
            state = HomeViewModel.State.Success(previewAgent),
            onNavigateToUsers = {},
            onNavigateToEvents = {},
            onNavigateToHouseholds = {},
            onNavigateToStats = {},
            onNavigateToSettings = {},
        )
    }
}
