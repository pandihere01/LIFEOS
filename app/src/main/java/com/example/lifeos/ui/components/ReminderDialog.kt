
package com.example.lifeos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeos.data.local.entity.ReminderEntity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ReminderDialog(
    existingReminder: ReminderEntity? = null,
    onDismiss: () -> Unit,
    onSave: (ReminderEntity) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Initial date and time
    val initialDateTime = remember(existingReminder) {
        existingReminder?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it.reminderTime),
                ZoneId.systemDefault()
            )
        } ?: LocalDateTime.now()
            .plusMinutes(5)
            .withSecond(0)
            .withNano(0)
    }

    // Form states
    var title by remember(existingReminder) {
        mutableStateOf(existingReminder?.title ?: "")
    }

    var description by remember(existingReminder) {
        mutableStateOf(existingReminder?.description ?: "")
    }

    var dateText by remember(existingReminder) {
        mutableStateOf(initialDateTime.toLocalDate().format(dateFormatter))
    }

    var timeText by remember(existingReminder) {
        mutableStateOf(initialDateTime.toLocalTime().format(timeFormatter))
    }

    var priority by remember(existingReminder) {
        mutableStateOf(existingReminder?.priority ?: "MEDIUM")
    }

    var recurrence by remember(existingReminder) {
        mutableStateOf(existingReminder?.recurrenceType ?: "NONE")
    }

    var notificationEnabled by remember(existingReminder) {
        mutableStateOf(existingReminder?.isNotificationEnabled ?: true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    // Date and time picker visibility
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val priorities = listOf("LOW", "MEDIUM", "HIGH")

    val recurrenceOptions = listOf(
        "NONE",
        "DAILY",
        "WEEKLY",
        "MONTHLY"
    )

    // Main reminder dialog
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (existingReminder == null) {
                    "Create Reminder"
                } else {
                    "Edit Reminder"
                }
            )
        },

        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Reminder title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        error = null
                    },
                    label = {
                        Text("Reminder title")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date selection
                Text(
                    text = "Reminder date",
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedTextField(
                    value = dateText,
                    onValueChange = {
                        dateText = it
                        error = null
                    },
                    label = {
                        Text("Date (yyyy-MM-dd)")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📅 Choose date")
                }

                // Time selection
                Text(
                    text = "Reminder time",
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedTextField(
                    value = timeText,
                    onValueChange = {
                        timeText = it
                        error = null
                    },
                    label = {
                        Text("Time (HH:mm)")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = {
                        showTimePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🕒 Choose time")
                }

                HorizontalDivider()

                // Priority
                Text(
                    "Priority",
                    style = MaterialTheme.typography.titleSmall
                )

                priorities.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = priority == option,
                            onClick = {
                                priority = option
                            }
                        )

                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                HorizontalDivider()

                // Recurrence
                Text(
                    "Repeat",
                    style = MaterialTheme.typography.titleSmall
                )

                recurrenceOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = recurrence == option,
                            onClick = {
                                recurrence = option
                            }
                        )

                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Notification
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = notificationEnabled,
                        onCheckedChange = {
                            notificationEnabled = it
                        }
                    )

                    Text("Enable notification")
                }

                // Validation error
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        if (title.isBlank()) {
                            error = "Please enter a reminder title."
                            return@TextButton
                        }

                        val parsedDate = LocalDate.parse(
                            dateText,
                            dateFormatter
                        )

                        val parsedTime = LocalTime.parse(
                            timeText,
                            timeFormatter
                        )

                        val parsedDateTime = LocalDateTime.of(
                            parsedDate,
                            parsedTime
                        )

                        val reminderMillis = parsedDateTime
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        if (reminderMillis <= System.currentTimeMillis()) {
                            error = "Choose a future date and time."
                            return@TextButton
                        }

                        onSave(
                            ReminderEntity(
                                id = existingReminder?.id ?: 0,
                                title = title.trim(),
                                description = description.trim(),
                                reminderTime = reminderMillis,
                                priority = priority,
                                isCompleted = existingReminder?.isCompleted
                                    ?: false,
                                isRecurring = recurrence != "NONE",
                                recurrenceType = recurrence,
                                isNotificationEnabled = notificationEnabled,
                                createdAt = existingReminder?.createdAt
                                    ?: System.currentTimeMillis()
                            )
                        )

                    } catch (exception: Exception) {
                        error = "Invalid date or time. Use yyyy-MM-dd and HH:mm."
                    }
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )

    // DATE PICKER
    if (showDatePicker) {

        val initialDateMillis = runCatching {
            LocalDate.parse(dateText, dateFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrElse {
            LocalDate.now()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dateText = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .format(dateFormatter)
                        }

                        error = null
                        showDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    // TIME PICKER
    if (showTimePicker) {

        val initialTime = runCatching {
            LocalTime.parse(timeText, timeFormatter)
        }.getOrDefault(LocalTime.now())

        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
            },

            title = {
                Text("Select reminder time")
            },

            text = {
                TimePicker(
                    state = timePickerState
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        timeText = "%02d:%02d".format(
                            timePickerState.hour,
                            timePickerState.minute
                        )

                        error = null
                        showTimePicker = false
                    }
                ) {
                    Text("Select")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
