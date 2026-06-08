package org.censusmate.mobile.presentation.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.R
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.usecase.event.GetEventUseCase
import org.censusmate.mobile.domain.usecase.event.UpdateEventUseCase
import org.censusmate.mobile.ui.components.DateTimePickerField
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun UpdateEventRoute(
    eventId: String,
    getEventUseCase: GetEventUseCase,
    updateEventUseCase: UpdateEventUseCase,
    onBack: () -> Unit,
    onUpdated: () -> Unit
) {
    val viewModel: UpdateEventViewModel = viewModel(
        factory = UpdateEventViewModel.factory(getEventUseCase, updateEventUseCase)
    )

    LaunchedEffect(eventId) { viewModel.loadEvent(eventId) }

    LaunchedEffect(viewModel.state) {
        if (viewModel.state is UpdateEventViewModel.State.Updated) onUpdated()
    }

    UpdateEventScreen(
        state = viewModel.state,
        onSubmit = { name, start, end -> viewModel.updateEvent(eventId, name, start, end) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateEventScreen(
    state: UpdateEventViewModel.State,
    onSubmit: (name: String, startDatetime: String, endDatetime: String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var startDatetime by remember { mutableStateOf("") }
    var endDatetime by remember { mutableStateOf("") }
    var fieldsInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is UpdateEventViewModel.State.Success && !fieldsInitialized) {
            name = state.event.name
            startDatetime = state.event.startDatetime
            endDatetime = state.event.endDatetime
            fieldsInitialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_event)) }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        }) { padding ->
        when (state) {
            is UpdateEventViewModel.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is UpdateEventViewModel.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            }

            is UpdateEventViewModel.State.Success, is UpdateEventViewModel.State.Updated -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.event_name)) },
                        leadingIcon = { Icon(Icons.Default.Event, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DateTimePickerField(
                        value = startDatetime,
                        onValueChange = { startDatetime = it },
                        label = stringResource(R.string.start_datetime),
                        modifier = Modifier.fillMaxWidth()
                    )

                    DateTimePickerField(
                        value = endDatetime,
                        onValueChange = { endDatetime = it },
                        label = stringResource(R.string.end_datetime),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onSubmit(name, startDatetime, endDatetime) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(stringResource(R.string.save), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

private val updateEventState = UpdateEventViewModel.State.Success(
    Event(
        "1", "Перепись 2026",
        "2026-01-01T00:00:00", "2026-12-31T23:59:59",
        true, "2026-01-01", null
    )
)

@Preview(showBackground = true, name = "Update Event Screen")
@Composable
private fun UpdateEventPreview() {
    MobileApplicationTheme {
        UpdateEventScreen(
            state = updateEventState,
            onSubmit = { _, _, _ -> },
            onBack = {}
        )
    }
}