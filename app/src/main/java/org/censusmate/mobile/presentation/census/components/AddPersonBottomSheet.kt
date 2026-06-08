package org.censusmate.mobile.presentation.census.components

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
import androidx.compose.ui.res.stringResource
import org.censusmate.mobile.R
import org.censusmate.mobile.presentation.census.PersonForm
import org.censusmate.mobile.ui.components.DatePickerField
import org.censusmate.mobile.ui.components.SheetSectionLabel

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
                text = stringResource(R.string.add_resident),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Пол
            SheetSectionLabel(stringResource(R.string.gender))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = gender == "male",
                    onClick = { gender = if (gender == "male") "" else "male" },
                    label = { Text(stringResource(R.string.gender_male_chip)) })
                FilterChip(
                    selected = gender == "female",
                    onClick = { gender = if (gender == "female") "" else "female" },
                    label = { Text(stringResource(R.string.gender_female_chip)) })
            }

            // Дата рождения
            DatePickerField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = stringResource(R.string.birth_date_label),
                modifier = Modifier.fillMaxWidth()
            )

            // Гражданство
            SheetSectionLabel(stringResource(R.string.citizenship))
            OutlinedTextField(
                value = citizenship,
                onValueChange = { citizenship = it },
                label = { Text(stringResource(R.string.citizenship)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SheetSectionLabel(stringResource(R.string.dual_citizenship))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = hasDualCitizenship == true, onClick = {
                    hasDualCitizenship = if (hasDualCitizenship == true) null else true
                }, label = { Text(stringResource(R.string.yes)) })
                FilterChip(selected = hasDualCitizenship == false, onClick = {
                    hasDualCitizenship = if (hasDualCitizenship == false) null else false
                }, label = { Text(stringResource(R.string.no)) })
            }

            // Язык
            SheetSectionLabel(stringResource(R.string.language_section))
            OutlinedTextField(
                value = nationality,
                onValueChange = { nationality = it },
                label = { Text(stringResource(R.string.nationality)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nativeLanguage,
                onValueChange = { nativeLanguage = it },
                label = { Text(stringResource(R.string.native_language)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SheetSectionLabel(stringResource(R.string.speaks_russian))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = speaksRussian == true,
                    onClick = { speaksRussian = if (speaksRussian == true) null else true },
                    label = { Text(stringResource(R.string.yes)) })
                FilterChip(
                    selected = speaksRussian == false,
                    onClick = { speaksRussian = if (speaksRussian == false) null else false },
                    label = { Text(stringResource(R.string.no)) })
            }

            OutlinedTextField(
                value = otherLanguages,
                onValueChange = { otherLanguages = it },
                label = { Text(stringResource(R.string.other_languages_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Образование
            SheetSectionLabel(stringResource(R.string.education))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "none" to R.string.no,
                    "primary" to R.string.primary_education,
                    "secondary" to R.string.secondary_education,
                    "higher" to R.string.higher_education
                ).forEach { (value, labelRes) ->
                    FilterChip(
                        selected = educationLevel == value,
                        onClick = { educationLevel = if (educationLevel == value) "" else value },
                        label = { Text(stringResource(labelRes)) })
                }
            }

            // Семейное положение
            SheetSectionLabel(stringResource(R.string.marital_status))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "single" to R.string.single_material_status,
                    "married" to R.string.marriage_material_statis,
                    "divorced" to R.string.divorced_material_status,
                    "widowed" to R.string.widower_or_widow_material_status
                ).forEach { (value, labelRes) ->
                    FilterChip(
                        selected = maritalStatus == value,
                        onClick = { maritalStatus = if (maritalStatus == value) "" else value },
                        label = { Text(stringResource(labelRes), style = MaterialTheme.typography.labelSmall) })
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = childrenCount,
                    onValueChange = { childrenCount = it },
                    label = { Text(stringResource(R.string.children_count_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = relationToHousehold,
                    onValueChange = { relationToHousehold = it },
                    label = { Text(stringResource(R.string.role_in_family)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Место рождения или проживания
            SheetSectionLabel(stringResource(R.string.birth_residence_section))
            OutlinedTextField(
                value = placeOfBirth,
                onValueChange = { placeOfBirth = it },
                label = { Text(stringResource(R.string.place_of_birth)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = currentResidence,
                onValueChange = { currentResidence = it },
                label = { Text(stringResource(R.string.current_residence)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Занятость
            SheetSectionLabel(stringResource(R.string.employment_status_section))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "employed" to R.string.employment_work,
                    "unemployed" to R.string.employment_unemployed,
                    "student" to R.string.employment_student,
                    "retired" to R.string.employment_retiree,
                    "other" to R.string.other
                ).forEach { (value, labelRes) ->
                    FilterChip(selected = employmentStatus == value, onClick = {
                        employmentStatus = if (employmentStatus == value) "" else value
                    }, label = { Text(stringResource(labelRes)) })
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
                            employmentStatus = employmentStatus.ifBlank { null })
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = birthDate.isNotBlank()
            ) {
                Text(stringResource(R.string.add))
            }
        }
    }
}