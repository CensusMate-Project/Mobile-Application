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
import androidx.compose.ui.unit.dp
import org.censusmate.mobile.domain.model.Stats

@Composable
fun OverviewSection(stats: Stats) {
    StatsSection(title = "Общая информация") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "Жителей",
                value = stats.totalPopulation.toString(),
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Домохозяйств",
                value = stats.totalHouseholds.toString(),
                icon = Icons.Default.Home,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Среднее на дом",
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
                label = "Средний возраст",
                value = "%.1f".format(stats.averageAge),
                icon = Icons.Default.Cake,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Детей (до 15)",
                value = stats.childrenCount.toString(),
                icon = Icons.Default.ChildCare,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Пожилых (65+)",
                value = stats.elderlyCount.toString(),
                icon = Icons.Default.Elderly,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DemographicsSection(stats: Stats) {
    StatsSection(title = "Демография") {
        val total = stats.genderDistribution.values.sum().toFloat()
        if (total > 0) {
            StatsBarLabel("Пол")
            stats.genderDistribution.forEach { (gender, count) ->
                DistributionBar(
                    label = if (gender == "male") "Мужчины" else "Женщины",
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
        StatsBarLabel("Семейное положение")
        val maritalTotal = stats.maritalStatusDistribution.values.sum().toFloat()
        stats.maritalStatusDistribution.forEach { (status, count) ->
            DistributionBar(
                label = when (status) {
                    "single" -> "Холост/не замужем"
                    "married" -> "В браке"
                    "divorced" -> "Разведён/а"
                    "widowed" -> "Вдовец/вдова"
                    else -> status
                },
                count = count,
                total = maritalTotal.coerceAtLeast(1f)
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = "Говорят по-русски",
                value = "%.1f%%".format(stats.percentSpeaksRussian),
                icon = Icons.Default.Language,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Двойное гражданство",
                value = stats.dualCitizenshipCount.toString(),
                icon = Icons.Default.Flag,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EducationSection(stats: Stats) {
    StatsSection(title = "Образование") {
        val total = stats.educationDistribution.values.sum().toFloat()
        stats.educationDistribution.forEach { (level, count) ->
            DistributionBar(
                label = when (level) {
                    "higher" -> "Высшее"
                    "secondary" -> "Среднее"
                    "primary" -> "Начальное"
                    else -> "Без образования"
                },
                count = count,
                total = total.coerceAtLeast(1f)
            )
        }
    }
}

@Composable
fun EmploymentSection(stats: Stats) {
    StatsSection(title = "Занятость") {
        val total = stats.employmentDistribution.values.sum().toFloat()
        stats.employmentDistribution.forEach { (status, count) ->
            DistributionBar(
                label = when (status) {
                    "employed" -> "Работает"
                    "unemployed" -> "Безработный"
                    "student" -> "Студент"
                    "retired" -> "Пенсионер"
                    else -> status
                },
                count = count,
                total = total.coerceAtLeast(1f)
            )
        }

        Spacer(Modifier.height(8.dp))
        StatsBarLabel("Источники дохода")
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
    StatsSection(title = "Жильё") {
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
                label = "Ср. общая пл.",
                value = "%.1f м²".format(stats.avgTotalArea),
                icon = Icons.Default.SquareFoot,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Ср. жилая пл.",
                value = "%.1f м²".format(stats.avgLivingArea),
                icon = Icons.Default.SquareFoot,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LanguagesSection(stats: Stats) {
    if (stats.topOtherLanguages.isEmpty()) return
    StatsSection(title = "Другие языки") {
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
