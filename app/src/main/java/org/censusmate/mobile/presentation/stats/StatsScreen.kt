package org.censusmate.mobile.presentation.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.censusmate.mobile.domain.model.Event
import org.censusmate.mobile.domain.model.LanguageCount
import org.censusmate.mobile.domain.model.Stats
import org.censusmate.mobile.domain.usecase.event.GetEventsUseCase
import org.censusmate.mobile.domain.usecase.stats.GetStatsUseCase
import org.censusmate.mobile.presentation.stats.components.DemographicsSection
import org.censusmate.mobile.presentation.stats.components.EducationSection
import org.censusmate.mobile.presentation.stats.components.EmploymentSection
import org.censusmate.mobile.presentation.stats.components.EventSelector
import org.censusmate.mobile.presentation.stats.components.HousingSection
import org.censusmate.mobile.presentation.stats.components.LanguagesSection
import org.censusmate.mobile.presentation.stats.components.OverviewSection
import org.censusmate.mobile.ui.theme.MobileApplicationTheme

@Composable
fun StatsRoute(
    getEventsUseCase: GetEventsUseCase,
    getStatsUseCase: GetStatsUseCase,
    onBack: () -> Unit
) {
    val viewModel: StatsViewModel = viewModel(
        factory = StatsViewModel.factory(getEventsUseCase, getStatsUseCase)
    )

    StatsScreen(
        state = viewModel.state,
        selectedEvent = viewModel.selectedEvent,
        onSelectEvent = viewModel::selectEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    state: StatsViewModel.State,
    selectedEvent: Event?,
    onSelectEvent: (Event) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") }, navigationIcon = {
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
            is StatsViewModel.State.LoadingEvents -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is StatsViewModel.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is StatsViewModel.State.EventsLoaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EventSelector(
                        events = state.events,
                        selectedEvent = selectedEvent,
                        onSelect = onSelectEvent
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Выберите событие для просмотра статистики",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is StatsViewModel.State.LoadingStats -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EventSelector(
                        events = state.events,
                        selectedEvent = selectedEvent,
                        onSelect = onSelectEvent
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }

            is StatsViewModel.State.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        EventSelector(
                            events = state.events,
                            selectedEvent = selectedEvent,
                            onSelect = onSelectEvent
                        )
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { OverviewSection(stats = state.stats) }
                        item { DemographicsSection(stats = state.stats) }
                        item { EducationSection(stats = state.stats) }
                        item { EmploymentSection(stats = state.stats) }
                        item { HousingSection(stats = state.stats) }
                        item { LanguagesSection(stats = state.stats) }
                    }
                }
            }

            StatsViewModel.State.Idle -> {}
        }
    }
}

private val previewEvents = listOf(
    Event(
        "1",
        "Перепись 2026",
        "2026-01-01T00:00:00",
        "2026-12-31T23:59:59",
        true,
        "2026-01-01",
        null
    )
)

private val previewStats = Stats(
    eventId = "1",
    eventName = "Перепись 2026",
    totalPopulation = 1250,
    totalHouseholds = 420,
    avgPersonsPerHousehold = 2.97,
    genderDistribution = mapOf("male" to 610L, "female" to 640L),
    averageAge = 38.5,
    childrenCount = 180,
    elderlyCount = 95,
    maritalStatusDistribution = mapOf(
        "married" to 680L,
        "single" to 320L,
        "divorced" to 180L,
        "widowed" to 70L
    ),
    avgChildrenCount = 1.4,
    educationDistribution = mapOf(
        "higher" to 450L,
        "secondary" to 620L,
        "primary" to 150L,
        "none" to 30L
    ),
    employmentDistribution = mapOf(
        "employed" to 720L,
        "retired" to 95L,
        "student" to 180L,
        "unemployed" to 255L
    ),
    incomeSourcesDistribution = mapOf("salary" to 720L, "pension" to 95L, "business" to 120L),
    percentSpeaksRussian = 94.5,
    dualCitizenshipCount = 23,
    topOtherLanguages = listOf(LanguageCount("Английский", 85), LanguageCount("Татарский", 42)),
    dwellingTypeDistribution = mapOf("apartment" to 310L, "house" to 95L, "room" to 15L),
    avgTotalArea = 58.3,
    avgLivingArea = 38.7
)

@Preview(showBackground = true, name = "Stats Success Screen")
@Composable
private fun StatsSuccessPreview() {
    MobileApplicationTheme {
        StatsScreen(
            state = StatsViewModel.State.Success(previewStats, previewEvents),
            selectedEvent = previewEvents.first(),
            onSelectEvent = {},
            onBack = {}
        )
    }
}