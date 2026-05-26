package org.censusmate.mobile.presentation.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.domain.usecase.event.CreateEventUseCase
import org.censusmate.mobile.ui.components.DateTimePickerField
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun CreateEventRoute(
    createEventUseCase: CreateEventUseCase,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val viewModel: CreateEventViewModel = viewModel(
        factory = CreateEventViewModel.factory(createEventUseCase)
    )

    LaunchedEffect(viewModel.state) {
        if (viewModel.state is CreateEventViewModel.State.Success) onCreated()
    }

    CreateEventScreen(
        state = viewModel.state,
        onSubmit = { name, start, end -> viewModel.createEvent(name, start, end) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    state: CreateEventViewModel.State,
    onSubmit: (name: String, startDatetime: String, endDatetime: String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var startDatetime by remember { mutableStateOf("") }
    var endDatetime by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новое событие") }, navigationIcon = {
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
                label = { Text("Название события *") },
                leadingIcon = { Icon(Icons.Default.Event, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is CreateEventViewModel.State.Loading
            )

            DateTimePickerField(
                value = startDatetime,
                onValueChange = { startDatetime = it },
                label = "Дата и время начала *",
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is CreateEventViewModel.State.Loading
            )

            DateTimePickerField(
                value = endDatetime,
                onValueChange = { endDatetime = it },
                label = "Дата и время окончания *",
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is CreateEventViewModel.State.Loading
            )

            AnimatedVisibility(visible = state is CreateEventViewModel.State.Error) {
                if (state is CreateEventViewModel.State.Error) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ), modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { onSubmit(name, startDatetime, endDatetime) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state !is CreateEventViewModel.State.Loading
            ) {
                if (state is CreateEventViewModel.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Создать событие", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Create Event Idle Screen")
@Composable
private fun CreateEventPreview() {
    MobileApplicationTheme {
        CreateEventScreen(
            state = CreateEventViewModel.State.Idle,
            onSubmit = { _, _, _ -> },
            onBack = {}
        )
    }
}