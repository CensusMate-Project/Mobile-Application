package org.censusmate.mobile.presentation.events

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
import androidx.compose.material.icons.filled.EventBusy
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
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.usecase.auth.GetMeUseCase
import org.censusmate.mobile.domain.usecase.event.DeleteEventUseCase
import org.censusmate.mobile.domain.usecase.event.GetEventsUseCase
import org.censusmate.mobile.presentation.events.components.EventCard
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun EventsRoute(
    getMeUseCase: GetMeUseCase,
    getEventsUseCase: GetEventsUseCase,
    deleteEventUseCase: DeleteEventUseCase,
    shouldRefresh: Boolean,
    onRefreshHandled: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: EventsViewModel = viewModel(
        factory = EventsViewModel.factory(getMeUseCase, getEventsUseCase, deleteEventUseCase)
    )

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadEvents(refresh = true)
            onRefreshHandled()
        }
    }

    EventsScreen(
        state = viewModel.state,
        onRefresh = { viewModel.loadEvents(refresh = true) },
        onLoadNextPage = { viewModel.loadNextPage() },
        onDelete = { id -> viewModel.deleteEvent(id) },
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToEdit = onNavigateToEdit,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    state: EventsViewModel.State,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onDelete: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onBack: () -> Unit
) {
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("События переписи") }, navigationIcon = {
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
        if (state is EventsViewModel.State.Success && state.isAdmin) {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Создать событие")
            }
        }
    }) { padding ->
        when (state) {
            is EventsViewModel.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is EventsViewModel.State.Error -> {
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

            is EventsViewModel.State.Success -> {
                if (state.events.isEmpty()) {
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
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (state.isAdmin) "Нет событий" else "Нет активных событий",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (state.isAdmin) {
                                Text(
                                    text = "Нажмите + чтобы создать",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                        items(state.events, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                isAdmin = state.isAdmin,
                                onEdit = { onNavigateToEdit(event.id) },
                                onDelete = { eventToDelete = event })
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

    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            icon = {
                Icon(
                    Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Удалить событие?") },
            text = { Text("Событие «${event.name}» будет удалено.") },
            confirmButton = {
                Button(
                    onClick = { onDelete(event.id); eventToDelete = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

private val previewEvents = listOf(
    Event(
        "1", "Перепись 2025",
        "2026-01-01T00:00:00", "2026-12-31T23:59:59",
        true, "2026-01-01T00:00:00", null
    )
)

@Preview(showBackground = true, name = "Events Screen")
@Composable
private fun EventsAdminPreview() {
    MobileApplicationTheme {
        EventsScreen(
            state = EventsViewModel.State.Success(previewEvents, isAdmin = true, isLastPage = true),
            onRefresh = {},
            onLoadNextPage = {},
            onDelete = {},
            onNavigateToCreate = {},
            onNavigateToEdit = {},
            onBack = {}
        )
    }
}