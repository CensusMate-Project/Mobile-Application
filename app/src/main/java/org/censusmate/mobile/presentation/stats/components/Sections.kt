package org.censusmate.mobile.presentation.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.censusmate.mobile.R
import org.censusmate.mobile.domain.model.Stats

@Composable
fun OverviewSection(stats: Stats) {
    StatsSection(title = stringResource(R.string.general_information)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = stringResource(R.string.residents),
                value = stats.totalPopulation.toString(),
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.households_genitive),
                value = stats.totalHouseholds.toString(),
                icon = Icons.Default.Home,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.average_per_house),
                value = "%.1f".format(stats.avgPersonsPerHousehold),
                icon = Icons.Default.Group,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = stringResource(R.string.average_age),
                value = "%.1f".format(stats.averageAge),
                icon = Icons.Default.Cake,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.children_under_15),
                value = stats.childrenCount.toString(),
                icon = Icons.Default.ChildCare,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.elderly_65),
                value = stats.elderlyCount.toString(),
                icon = Icons.Default.Elderly,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DemographicsSection(stats: Stats) {
    StatsSection(title = stringResource(R.string.demographics)) {
        val total = stats.genderDistribution.values.sum().toFloat()
        if (total > 0) {
            StatsBarLabel(stringResource(R.string.gender))
            stats.genderDistribution.forEach { (gender, count) ->
                DistributionBar(
                    label = if (gender == "male")
                        stringResource(R.string.males)
                    else
                        stringResource(R.string.females),
                    count = count,
                    total = total,
                    color = if (gender == "male")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        StatsBarLabel(stringResource(R.string.marital_status))
        val maritalTotal = stats.maritalStatusDistribution.values.sum().toFloat()
        stats.maritalStatusDistribution.forEach { (status, count) ->
            DistributionBar(
                label = when (status) {
                    "single" -> stringResource(R.string.single_material_status)
                    "married" -> stringResource(R.string.marriage_material_statis)
                    "divorced" -> stringResource(R.string.divorced_material_status)
                    "widowed" -> stringResource(R.string.widower_or_widow_material_status)
                    else -> status
                },
                count = count,
                total = maritalTotal.coerceAtLeast(1f)
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = stringResource(R.string.speaks_russia),
                value = "%.1f%%".format(stats.percentSpeaksRussian),
                icon = Icons.Default.Language,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.dual_citizenship),
                value = stats.dualCitizenshipCount.toString(),
                icon = Icons.Default.Flag,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EducationSection(stats: Stats) {
    StatsSection(title = stringResource(R.string.education)) {
        val total = stats.educationDistribution.values.sum().toFloat()
        stats.educationDistribution.forEach { (level, count) ->
            DistributionBar(
                label = when (level) {
                    "higher" -> stringResource(R.string.higher_education)
                    "secondary" -> stringResource(R.string.secondary_education)
                    "primary" -> stringResource(R.string.primary_education)
                    else -> stringResource(R.string.without_education)
                },
                count = count,
                total = total.coerceAtLeast(1f)
            )
        }
    }
}

@Composable
fun EmploymentSection(stats: Stats) {
    StatsSection(title = stringResource(R.string.employment)) {
        val total = stats.employmentDistribution.values.sum().toFloat()
        stats.employmentDistribution.forEach { (status, count) ->
            DistributionBar(
                label = when (status) {
                    "employed" -> stringResource(R.string.employment_work)
                    "unemployed" -> stringResource(R.string.employment_unemployed)
                    "student" -> stringResource(R.string.employment_student)
                    "retired" -> stringResource(R.string.employment_retiree)
                    else -> status
                },
                count = count,
                total = total.coerceAtLeast(1f)
            )
        }

        Spacer(Modifier.height(8.dp))
        StatsBarLabel(stringResource(R.string.sources_of_income))
        val incomeTotal = stats.incomeSourcesDistribution.values.sum().toFloat()
        stats.incomeSourcesDistribution.forEach { (source, count) ->
            DistributionBar(
                label = source,
                count = count,
                total = incomeTotal.coerceAtLeast(1f)
            )
        }
    }
}

@Composable
fun HousingSection(stats: Stats) {
    StatsSection(title = stringResource(R.string.housing)) {
        val total = stats.dwellingTypeDistribution.values.sum().toFloat()
        stats.dwellingTypeDistribution.forEach { (type, count) ->
            DistributionBar(
                label = type,
                count = count,
                total = total.coerceAtLeast(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = stringResource(R.string.wed_general_square),
                value = stringResource(R.string.area_m2, "%.1f".format(stats.avgTotalArea)),
                icon = Icons.Default.SquareFoot,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.wed_residential_area),
                value = stringResource(R.string.area_m2, "%.1f".format(stats.avgLivingArea)),
                icon = Icons.Default.SquareFoot,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LanguagesSection(stats: Stats) {
    if (stats.topOtherLanguages.isEmpty()) return
    StatsSection(title = stringResource(R.string.other_languages)) {
        val maxCount = stats.topOtherLanguages.maxOf { it.count }.toFloat()
        stats.topOtherLanguages.forEach { lang ->
            DistributionBar(
                label = lang.language,
                count = lang.count,
                total = maxCount.coerceAtLeast(1f)
            )
        }
    }
}
