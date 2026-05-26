package org.censusmate.mobile.presentation.census

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.domain.model.Address
import org.censusmate.mobile.domain.repository.AddressRepository
import org.censusmate.mobile.domain.usecase.address.SuggestAddressUseCase
import org.censusmate.mobile.domain.usecase.household.CreateHouseholdUseCase
import org.censusmate.mobile.domain.usecase.person.CreatePersonUseCase
import org.censusmate.mobile.presentation.census.components.AddressField
import org.censusmate.mobile.presentation.census.components.PersonFormCard
import org.censusmate.mobile.ui.components.SectionTitle
import org.censusmate.mobile.ui.theme.MobileApplicationTheme
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCensusRoute(
    createHouseholdUseCase: CreateHouseholdUseCase,
    createPersonUseCase: CreatePersonUseCase,
    suggestAddressUseCase: SuggestAddressUseCase,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    val viewModel: CreateCensusViewModel = viewModel(
        factory = CreateCensusViewModel.factory(createHouseholdUseCase, createPersonUseCase)
    )

    LaunchedEffect(viewModel.state) {
        if (viewModel.state is CreateCensusViewModel.State.Success) onCreated()
    }

    CreateCensusScreen(
        state = viewModel.state,
        persons = viewModel.persons,
        suggestAddressUseCase = suggestAddressUseCase,
        onAddPerson = viewModel::addPerson,
        onRemovePerson = viewModel::removePerson,
        onSubmit = viewModel::submit,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCensusScreen(
    state: CreateCensusViewModel.State,
    persons: List<PersonForm>,
    suggestAddressUseCase: SuggestAddressUseCase,
    onAddPerson: (PersonForm) -> Unit,
    onRemovePerson: (Int) -> Unit,
    onSubmit: (String, Int, String?, String?, Int?, Int?, Int?, String?) -> Unit,
    onBack: () -> Unit
) {
    var address by remember { mutableStateOf("") }
    var totalResidents by remember { mutableStateOf("") }
    var dwellingType by remember { mutableStateOf("") }
    var buildingYear by remember { mutableStateOf("") }
    var totalArea by remember { mutableStateOf("") }
    var livingArea by remember { mutableStateOf("") }
    var roomsCount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showAddPersonSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новое домохозяйство") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Address
            SectionTitle("Адрес")

            AddressField(
                value = address,
                onValueChange = { address = it },
                suggestAddressUseCase = suggestAddressUseCase
            )

            // Household
            SectionTitle("Жильё")

            OutlinedTextField(
                value = totalResidents,
                onValueChange = { totalResidents = it },
                label = { Text("Количество жителей *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dwellingType,
                    onValueChange = { dwellingType = it },
                    label = { Text("Тип жилья") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = buildingYear,
                    onValueChange = { buildingYear = it },
                    label = { Text("Год постройки") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = totalArea,
                    onValueChange = { totalArea = it },
                    label = { Text("Общая пл. (м²)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = livingArea,
                    onValueChange = { livingArea = it },
                    label = { Text("Жилая пл. (м²)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = roomsCount,
                    onValueChange = { roomsCount = it },
                    label = { Text("Комнат") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Примечания") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // Persons
            SectionTitle("Жители (${persons.size})")

            persons.forEachIndexed { index, person ->
                PersonFormCard(
                    person = person,
                    onRemove = { onRemovePerson(index) }
                )
            }

            OutlinedButton(
                onClick = { showAddPersonSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(Modifier.width(8.dp))
                Text("Добавить жителя")
            }

            // Error
            AnimatedVisibility(visible = state is CreateCensusViewModel.State.Error) {
                if (state is CreateCensusViewModel.State.Error) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Creation button
            Button(
                onClick = {
                    onSubmit(
                        address,
                        totalResidents.toIntOrNull() ?: 0,
                        dwellingType.ifBlank { null },
                        buildingYear.ifBlank { null },
                        totalArea.toIntOrNull(),
                        livingArea.toIntOrNull(),
                        roomsCount.toIntOrNull(),
                        notes.ifBlank { null }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state !is CreateCensusViewModel.State.Loading
            ) {
                if (state is CreateCensusViewModel.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Создать", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    if (showAddPersonSheet) {
        AddPersonBottomSheet(
            onDismiss = { showAddPersonSheet = false },
            onConfirm = { person ->
                onAddPerson(person)
                showAddPersonSheet = false
            }
        )
    }
}

private val fakeAddressRepository = object : AddressRepository {
    override suspend fun suggest(query: String, count: Int): Result<List<Address>> =
        Result.success(emptyList())
}

@Preview(showBackground = true, name = "Create Census Idle Screen")
@Composable
private fun CreateCensusIdlePreview() {
    MobileApplicationTheme {
        CreateCensusScreen(
            state                 = CreateCensusViewModel.State.Idle,
            persons               = listOf(
                PersonForm(gender = "female", birthDate = "1992-05-20", citizenship = "Россия")
            ),
            suggestAddressUseCase = SuggestAddressUseCase(fakeAddressRepository),
            onAddPerson           = {},
            onRemovePerson        = {},
            onSubmit              = { _, _, _, _, _, _, _, _ -> },
            onBack                = {}
        )
    }
}