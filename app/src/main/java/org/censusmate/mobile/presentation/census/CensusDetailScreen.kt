package org.censusmate.mobile.presentation.census

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.R
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.domain.usecase.household.GetHouseholdUseCase
import org.censusmate.mobile.domain.usecase.person.CreatePersonUseCase
import org.censusmate.mobile.domain.usecase.person.DeletePersonUseCase
import org.censusmate.mobile.domain.usecase.person.GetPersonsUseCase
import org.censusmate.mobile.presentation.census.components.AddPersonBottomSheet
import org.censusmate.mobile.presentation.census.components.HouseholdInfoCard
import org.censusmate.mobile.presentation.census.components.PersonCard

@Composable
fun CensusDetailRoute(
    householdId: String,
    getHouseholdUseCase: GetHouseholdUseCase,
    getPersonsUseCase: GetPersonsUseCase,
    deletePersonUseCase: DeletePersonUseCase,
    createPersonUseCase: CreatePersonUseCase,
    onBack: () -> Unit
) {
    val viewModel: CensusDetailViewModel = viewModel(
        factory = CensusDetailViewModel.factory(
            getHouseholdUseCase, getPersonsUseCase, deletePersonUseCase, createPersonUseCase
        )
    )

    LaunchedEffect(householdId) { viewModel.load(householdId) }

    CensusDetailScreen(
        state = viewModel.state,
        onDeletePerson = { personId -> viewModel.deletePerson(personId, householdId) },
        onAddPerson = { form -> viewModel.addPerson(householdId, form) },
        onRefresh = { viewModel.load(householdId) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CensusDetailScreen(
    state: CensusDetailViewModel.State,
    onDeletePerson: (String) -> Unit,
    onAddPerson: (PersonForm) -> Unit,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    var personToDelete by remember { mutableStateOf<Person?>(null) }
    var showAddPersonSheet by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = {
            Text(
                text = if (state is CensusDetailViewModel.State.Success) state.household.address
                else stringResource(R.string.household), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }, actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
        )
    }, floatingActionButton = {
        if (state is CensusDetailViewModel.State.Success) {
            FloatingActionButton(onClick = { showAddPersonSheet = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = stringResource(R.string.add_resident))
            }
        }
    }) { padding ->
        when (state) {
            is CensusDetailViewModel.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is CensusDetailViewModel.State.Error -> {
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
                        Button(onClick = onRefresh) { Text(stringResource(R.string.repeat)) }
                    }
                }
            }

            is CensusDetailViewModel.State.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Household info
                    item {
                        HouseholdInfoCard(household = state.household)
                    }

                    // Persons header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.residents_section, state.persons.size),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                    }

                    // Persons list
                    if (state.persons.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = stringResource(R.string.no_residents),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = stringResource(R.string.press_plus_to_add),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.persons, key = { it.id }) { person ->
                            PersonCard(
                                person = person, onDelete = { personToDelete = person })
                        }
                    }
                }
            }
        }
    }

    // Delete person confirmation dialog
    personToDelete?.let { person ->
        AlertDialog(
            onDismissRequest = { personToDelete = null },
            icon = {
                Icon(
                    Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.delete_person_title)) },
            text = { Text(stringResource(R.string.delete_person_confirmation)) },
            confirmButton = {
                Button(
                    onClick = { onDeletePerson(person.id); personToDelete = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { personToDelete = null }) { Text(stringResource(R.string.cancel)) }
            })
    }

    // Add person bottom sheet
    if (showAddPersonSheet) {
        AddPersonBottomSheet(onDismiss = { showAddPersonSheet = false }, onConfirm = { form ->
            onAddPerson(form)
            showAddPersonSheet = false
        })
    }
}