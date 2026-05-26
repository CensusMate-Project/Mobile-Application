package org.censusmate.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed && enabled) showDatePicker = true
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = if (value.isBlank()) "" else value.toDisplayDateTime(),
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
            readOnly = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showDatePicker = true }
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value.toDateTimeMillis() ?: System.currentTimeMillis()
        )

        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(
                onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                    showTimePicker = true
                }
            ) { Text("Далее") }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
        }) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = value.toHour(), initialMinute = value.toMinute(), is24Hour = true
        )

        TimePickerDialog(onDismiss = { showTimePicker = false }, onConfirm = {
            val dateMillis = selectedDateMillis ?: return@TimePickerDialog
            val result = buildDateTime(
                dateMillis = dateMillis,
                hour = timePickerState.hour,
                minute = timePickerState.minute
            )
            onValueChange(result)
            showTimePicker = false
        }) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit, content: @Composable () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = onConfirm) { Text("Выбрать") }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("Отмена") }
    }, text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Выберите время",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )
            content()
        }
    })
}

private fun buildDateTime(dateMillis: Long, hour: Int, minute: Int): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val year = cal.get(Calendar.YEAR)
    val month = (cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    val day = cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val h = hour.toString().padStart(2, '0')
    val m = minute.toString().padStart(2, '0')
    return "$year-$month-${day}T$h:$m:00"
}

private fun String.toDisplayDateTime(): String {
    return try {
        if (this.isBlank()) return ""
        val datePart = this.take(10).split("-")
        val timePart = this.drop(11).take(5)
        "${datePart[2]}.${datePart[1]}.${datePart[0]}, $timePart"
    } catch (e: Exception) {
        this
    }
}

private fun String.toDateTimeMillis(): Long? {
    return try {
        if (this.isBlank()) return null
        val parts = this.take(10).split("-")
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    } catch (e: Exception) {
        null
    }
}

private fun String.toHour(): Int = try {
    this.drop(11).take(2).toInt()
} catch (e: Exception) {
    0
}

private fun String.toMinute(): Int = try {
    this.drop(14).take(2).toInt()
} catch (e: Exception) {
    0
}