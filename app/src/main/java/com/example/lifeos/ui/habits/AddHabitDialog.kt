
package com.example.lifeos.ui.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeos.data.local.entity.HabitEntity
import com.example.lifeos.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId

private val weekdayOptions = listOf(
    "Monday", "Tuesday", "Wednesday",
    "Thursday", "Friday", "Saturday", "Sunday"
)

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (HabitEntity) -> Unit,
    existingHabit: HabitEntity? = null
) {
    var name by remember(existingHabit) {
        mutableStateOf(existingHabit?.name ?: "")
    }

    var category by remember(existingHabit) {
        mutableStateOf(existingHabit?.category ?: "Personal")
    }

    var frequency by remember(existingHabit) {
        mutableStateOf(existingHabit?.frequency ?: "DAILY")
    }

    var selectedDays by remember(existingHabit) {
        mutableStateOf(
            existingHabit?.selectedDays
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        )
    }

    var weeklyTarget by remember(existingHabit) {
        mutableIntStateOf(existingHabit?.targetPerWeek ?: 4)
    }

    var measurable by remember(existingHabit) {
        mutableStateOf(existingHabit?.targetValue != null)
    }

    var targetValue by remember(existingHabit) {
        mutableStateOf(
            existingHabit?.targetValue?.toString() ?: "1"
        )
    }

    var unit by remember(existingHabit) {
        mutableStateOf(
            existingHabit?.unit?.takeIf { it.isNotBlank() } ?: "times"
        )
    }

    var description by remember(existingHabit) {
        mutableStateOf(existingHabit?.description ?: "")
    }

    val validTarget =
        targetValue.toDoubleOrNull()?.let { it > 0 } ?: false

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existingHabit == null) "Create habit" else "Edit habit",
                color = LifeOSText
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Schedule", color = LifeOSText)

                Column {
                    listOf(
                        "DAILY" to "Every day",
                        "SELECTED_DAYS" to "Selected weekdays",
                        "WEEKLY_TARGET" to "Flexible weekly target"
                    ).forEach { (value, label) ->
                        Row {
                            RadioButton(
                                selected = frequency == value,
                                onClick = { frequency = value }
                            )

                            Text(
                                label,
                                modifier = Modifier.padding(top = 12.dp),
                                color = LifeOSText
                            )
                        }
                    }
                }

                if (frequency == "SELECTED_DAYS") {
                    Text("Choose days", color = LifeOSText)

                    weekdayOptions.forEachIndexed { index, day ->
                        Row {
                            Checkbox(
                                checked = index + 1 in selectedDays,
                                onCheckedChange = { checked ->
                                    selectedDays =
                                        if (checked) {
                                            selectedDays + (index + 1)
                                        } else {
                                            selectedDays - (index + 1)
                                        }
                                }
                            )

                            Text(
                                day,
                                modifier = Modifier.padding(top = 12.dp),
                                color = LifeOSText
                            )
                        }
                    }
                }

                if (frequency == "WEEKLY_TARGET") {
                    Text(
                        "Target: $weeklyTarget days per week",
                        color = LifeOSText
                    )

                    Slider(
                        value = weeklyTarget.toFloat(),
                        onValueChange = {
                            weeklyTarget = it.toInt()
                        },
                        valueRange = 1f..7f,
                        steps = 5
                    )
                }

                HorizontalDivider()

                Text("Habit type", color = LifeOSText)

                Row {
                    Checkbox(
                        checked = measurable,
                        onCheckedChange = { measurable = it }
                    )

                    Text(
                        "Measurable habit",
                        modifier = Modifier.padding(top = 12.dp),
                        color = LifeOSText
                    )
                }

                if (measurable) {
                    OutlinedTextField(
                        value = targetValue,
                        onValueChange = { targetValue = it },
                        label = { Text("Target value") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = {
                            Text("Unit (litres, minutes, pages...)")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startTimestamp =
                        existingHabit?.startDate
                            ?: LocalDate.now()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()

                    val updatedHabit = HabitEntity(
                        id = existingHabit?.id ?: 0,
                        name = name.trim(),
                        description = description.trim(),
                        category = category.trim().ifBlank {
                            "Personal"
                        },
                        frequency = frequency,
                        targetPerWeek = weeklyTarget,
                        selectedDays = selectedDays
                            .sorted()
                            .joinToString(","),
                        targetValue = if (measurable) {
                            targetValue.toDoubleOrNull()
                        } else {
                            null
                        },
                        unit = if (measurable) unit.trim() else "",
                        startDate = startTimestamp,
                        endDate = existingHabit?.endDate,
                        reminderEnabled =
                            existingHabit?.reminderEnabled ?: false,
                        reminderTime =
                            existingHabit?.reminderTime ?: "",
                        isActive = existingHabit?.isActive ?: true,
                        createdAt =
                            existingHabit?.createdAt
                                ?: System.currentTimeMillis()
                    )

                    onSave(updatedHabit)
                },
                enabled = name.isNotBlank() &&
                        (
                                frequency != "SELECTED_DAYS" ||
                                        selectedDays.isNotEmpty()
                                ) &&
                        (
                                !measurable ||
                                        (validTarget && unit.isNotBlank())
                                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LifeOSPurple
                )
            ) {
                Text(
                    if (existingHabit == null) {
                        "Save habit"
                    } else {
                        "Update habit"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LifeOSMuted)
            }
        },
        containerColor = LifeOSSurface
    )
}
