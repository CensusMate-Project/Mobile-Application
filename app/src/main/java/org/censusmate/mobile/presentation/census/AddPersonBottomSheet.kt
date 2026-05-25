package org.censusmate.mobile.presentation.census

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.censusmate.mobile.presentation.census.elements.SheetSectionLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (PersonForm) -> Unit
) {
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var citizenship by remember { mutableStateOf("") }
    var hasDualCitizenship by remember { mutableStateOf<Boolean?>(null) }
    var nationality by remember { mutableStateOf("") }
    var nativeLanguage by remember { mutableStateOf("") }
    var speaksRussian by remember { mutableStateOf<Boolean?>(null) }
    var otherLanguages by remember { mutableStateOf("") }
    var educationLevel by remember { mutableStateOf("") }
    var maritalStatus by remember { mutableStateOf("") }
    var childrenCount by remember { mutableStateOf("") }
    var relationToHousehold by remember { mutableStateOf("") }
    var placeOfBirth by remember { mutableStateOf("") }
    var currentResidence by remember { mutableStateOf("") }
    var employmentStatus by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Добавить жителя",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Пол
            SheetSectionLabel("Пол")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = gender == "male",
                    onClick = { gender = if (gender == "male") "" else "male" },
                    label = { Text("Мужской") })
                FilterChip(
                    selected = gender == "female",
                    onClick = { gender = if (gender == "female") "" else "female" },
                    label = { Text("Женский") })
            }

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Дата рождения * (ГГГГ-ММ-ДД)") },
                placeholder = { Text("1990-01-15") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Гражданство
            SheetSectionLabel("Гражданство")
            OutlinedTextField(
                value = citizenship,
                onValueChange = { citizenship = it },
                label = { Text("Гражданство") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SheetSectionLabel("Двойное гражданство")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = hasDualCitizenship == true, onClick = {
                    hasDualCitizenship = if (hasDualCitizenship == true) null else true
                }, label = { Text("Да") })
                FilterChip(selected = hasDualCitizenship == false, onClick = {
                    hasDualCitizenship = if (hasDualCitizenship == false) null else false
                }, label = { Text("Нет") })
            }

            // Язык
            SheetSectionLabel("Язык")
            OutlinedTextField(
                value = nationality,
                onValueChange = { nationality = it },
                label = { Text("Национальность") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nativeLanguage,
                onValueChange = { nativeLanguage = it },
                label = { Text("Родной язык") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SheetSectionLabel("Говорит по-русски")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = speaksRussian == true,
                    onClick = { speaksRussian = if (speaksRussian == true) null else true },
                    label = { Text("Да") })
                FilterChip(
                    selected = speaksRussian == false,
                    onClick = { speaksRussian = if (speaksRussian == false) null else false },
                    label = { Text("Нет") })
            }

            OutlinedTextField(
                value = otherLanguages,
                onValueChange = { otherLanguages = it },
                label = { Text("Другие языки (через запятую)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Образование
            SheetSectionLabel("Образование")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "none" to "Нет",
                    "primary" to "Начальное",
                    "secondary" to "Среднее",
                    "higher" to "Высшее"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = educationLevel == value,
                        onClick = { educationLevel = if (educationLevel == value) "" else value },
                        label = { Text(label) })
                }
            }

            // Семейное положение
            SheetSectionLabel("Семейное положение")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "single" to "Холост/не замужем",
                    "married" to "В браке",
                    "divorced" to "Разведён/а",
                    "widowed" to "Вдовец/вдова"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = maritalStatus == value,
                        onClick = { maritalStatus = if (maritalStatus == value) "" else value },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) })
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = childrenCount,
                    onValueChange = { childrenCount = it },
                    label = { Text("Детей") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = relationToHousehold,
                    onValueChange = { relationToHousehold = it },
                    label = { Text("Роль в семье") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Место рождения или проживания
            SheetSectionLabel("Место рождения / проживания")
            OutlinedTextField(
                value = placeOfBirth,
                onValueChange = { placeOfBirth = it },
                label = { Text("Место рождения") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = currentResidence,
                onValueChange = { currentResidence = it },
                label = { Text("Текущее место проживания") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Занятость
            SheetSectionLabel("Статус занятости")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "employed" to "Работает",
                    "unemployed" to "Безработный",
                    "student" to "Студент",
                    "retired" to "Пенсионер",
                    "other" to "Другое"
                ).forEach { (value, label) ->
                    FilterChip(selected = employmentStatus == value, onClick = {
                        employmentStatus = if (employmentStatus == value) "" else value
                    }, label = { Text(label) })
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onConfirm(
                        PersonForm(
                        gender = gender.ifBlank { null },
                        birthDate = birthDate,
                        citizenship = citizenship.ifBlank { null },
                        hasDualCitizenship = hasDualCitizenship,
                        nationality = nationality.ifBlank { null },
                        nativeLanguage = nativeLanguage.ifBlank { null },
                        speaksRussian = speaksRussian,
                        otherLanguages = otherLanguages.split(",").map { it.trim() }
                            .filter { it.isNotBlank() },
                        educationLevel = educationLevel.ifBlank { null },
                        maritalStatus = maritalStatus.ifBlank { null },
                        childrenCount = childrenCount.toIntOrNull(),
                        relationToHousehold = relationToHousehold.ifBlank { null },
                        placeOfBirth = placeOfBirth.ifBlank { null },
                        currentResidence = currentResidence.ifBlank { null },
                        employmentStatus = employmentStatus.ifBlank { null }))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = birthDate.isNotBlank()
            ) {
                Text("Добавить")
            }
        }
    }
}