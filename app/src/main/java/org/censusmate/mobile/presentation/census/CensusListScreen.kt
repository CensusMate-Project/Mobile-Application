package org.censusmate.mobile.presentation.census

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.domain.model.Household
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.household.DeleteHouseholdUseCase
import org.censusmate.mobile.domain.usecase.household.GetHouseholdsUseCase
import org.censusmate.mobile.presentation.census.components.HouseholdCard
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun CensusListRoute(
    getMeUseCase: GetMeUseCase,
    getHouseholdsUseCase: GetHouseholdsUseCase,
    deleteHouseholdUseCase: DeleteHouseholdUseCase,
    shouldRefresh: Boolean,
    onRefreshHandled: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: CensusListViewModel = viewModel(
        factory = CensusListViewModel.factory(
            getMeUseCase, getHouseholdsUseCase, deleteHouseholdUseCase
        )
    )

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadHouseholds(refresh = true)
            onRefreshHandled()
        }
    }

    CensusListScreen(
        state = viewModel.state,
        onRefresh = { viewModel.loadHouseholds(refresh = true) },
        onLoadNextPage = { viewModel.loadNextPage() },
        onDelete = { id -> viewModel.deleteHousehold(id) },
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToDetail = onNavigateToDetail,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CensusListScreen(
    state: CensusListViewModel.State,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onDelete: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    var householdToDelete by remember { mutableStateOf<Household?>(null) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Домохозяйства") }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }, actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Обновить")
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = onNavigateToCreate) {
            Icon(Icons.Default.Add, contentDescription = "Добавить")
        }
    }) { padding ->
        when (state) {
            is CensusListViewModel.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is CensusListViewModel.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRefresh) { Text("Повторить") }
                    }
                }
            }

            is CensusListViewModel.State.Success -> {
                if (state.households.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Нет домохозяйств",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Нажмите + чтобы добавить",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.households, key = { it.id }) { household ->
                            HouseholdCard(
                                household = household,
                                isAdmin = state.isAdmin,
                                onClick = { onNavigateToDetail(household.id) },
                                onDelete = { householdToDelete = household })
                        }

                        if (!state.isLastPage) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(onClick = onLoadNextPage) {
                                        Text("Загрузить ещё")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    householdToDelete?.let { household ->
        AlertDialog(onDismissRequest = { householdToDelete = null }, icon = {
            Icon(
                Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error
            )
        }, title = { Text("Удалить домохозяйство?") }, text = {
            Text("Домохозяйство по адресу «${household.address}» и все его жители будут удалены.")
        }, confirmButton = {
            Button(
                onClick = { onDelete(household.id); householdToDelete = null },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Удалить") }
        }, dismissButton = {
            TextButton(onClick = { householdToDelete = null }) { Text("Отмена") }
        })
    }
}

@Preview(showBackground = true, name = "CensusList — Success Admin")
@Composable
private fun CensusListAdminPreview() {
    MobileApplicationTheme {
        CensusListScreen(
            state = CensusListViewModel.State.Success(
                households = emptyList(),
                isAdmin = true,
                isLastPage = true
            ),
            onRefresh = {},
            onLoadNextPage = {},
            onDelete = {},
            onNavigateToCreate = {},
            onNavigateToDetail = {},
            onBack = {}
        )
    }
}