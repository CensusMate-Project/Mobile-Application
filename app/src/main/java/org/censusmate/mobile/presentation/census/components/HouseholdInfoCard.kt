package org.censusmate.mobile.presentation.census.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import org.censusmate.mobile.R
import org.censusmate.mobile.domain.model.Household

@Composable
fun HouseholdInfoCard(household: Household) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = household.address,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HouseholdInfoItem(
                    label = stringResource(R.string.residents), value = "${household.totalResidents}"
                )
                household.dwellingType?.let {
                    HouseholdInfoItem(label = stringResource(R.string.dwelling_type_label), value = it)
                }
                household.buildingYear?.let {
                    HouseholdInfoItem(label = stringResource(R.string.building_year_label), value = it)
                }
                household.roomsCount?.let {
                    HouseholdInfoItem(label = stringResource(R.string.rooms_count_label), value = "$it")
                }
            }

            if (household.totalArea != null || household.livingArea != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    household.totalArea?.let {
                        HouseholdInfoItem(label = stringResource(R.string.total_area_label), value = stringResource(R.string.area_m2, "$it"))
                    }
                    household.livingArea?.let {
                        HouseholdInfoItem(label = stringResource(R.string.living_area_label), value = stringResource(R.string.area_m2, "$it"))
                    }
                }
            }

            household.notes?.let { notes ->
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}