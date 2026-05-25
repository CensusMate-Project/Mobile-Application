package org.censusmate.mobile.presentation.census.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.censusmate.mobile.domain.model.Address
import org.censusmate.mobile.domain.usecase.address.SuggestAddressUseCase

@Composable
fun AddressField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestAddressUseCase: SuggestAddressUseCase
) {
    var suggestions by remember { mutableStateOf<List<Address>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (value.length >= 3) {
            suggestAddressUseCase(value).onSuccess {
                    suggestions = it
                    showSuggestions = it.isNotEmpty()
                }
        } else {
            showSuggestions = false
        }
    }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it); showSuggestions = false },
            label = { Text("Адрес *") },
            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = showSuggestions) {
            Card(modifier = Modifier.fillMaxWidth()) {
                suggestions.forEach { suggestion ->
                    ListItem(headlineContent = { Text(suggestion.value) }, supportingContent = {
                        Text(
                            text = suggestion.unrestrictedValue,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }, modifier = Modifier.clickable {
                        onValueChange(suggestion.unrestrictedValue)
                        showSuggestions = false
                    })
                    HorizontalDivider()
                }
            }
        }
    }
}