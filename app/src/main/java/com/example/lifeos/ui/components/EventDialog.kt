
package com.example.lifeos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeos.data.local.entity.CalendarEventEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    selectedDate: LocalDate,
    existingEvent: CalendarEventEntity? = null,
    onDismiss: () -> Unit,
    onSave: (CalendarEventEntity) -> Unit,
    onDelete: (CalendarEventEntity) -> Unit
) {
    var title by remember(existingEvent) {
        mutableStateOf(existingEvent?.title ?: "")
    }

    var description by remember(existingEvent) {
        mutableStateOf(existingEvent?.description ?: "")
    }

    var location by remember(existingEvent) {
        mutableStateOf(existingEvent?.location ?: "")
    }

    var isAllDay by remember(existingEvent) {
        mutableStateOf(existingEvent?.isAllDay ?: false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    var startTime by remember(existingEvent) {
        mutableStateOf(
            existingEvent?.let {
                LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(it.startTime),
                    ZoneId.systemDefault()
                ).toLocalTime().format(timeFormatter)
            } ?: "09:00"
        )
    }

    var endTime by remember(existingEvent) {
        mutableStateOf(
            existingEvent?.let {
                LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(it.endTime),
                    ZoneId.systemDefault()
                ).toLocalTime().format(timeFormatter)
            } ?: "10:00"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (existingEvent == null) {
                    "Create Event"
                } else {
                    "Edit Event"
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event title") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true
                )

                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAllDay,
                        onCheckedChange = { isAllDay = it }
                    )

                    Text(
                        text = "All day",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                if (!isAllDay) {

                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start time (HH:mm)") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End time (HH:mm)") },
                        singleLine = true
                    )
                }

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (existingEvent != null) {
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = true
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }

                TextButton(
                    onClick = {
                        try {
                            if (title.isBlank()) {
                                error = "Please enter an event title."
                                return@TextButton
                            }

                            val zone = ZoneId.systemDefault()

                            val startDateTime = if (isAllDay) {
                                selectedDate.atStartOfDay()
                            } else {
                                LocalDateTime.of(
                                    selectedDate,
                                    LocalTime.parse(startTime, timeFormatter)
                                )
                            }

                            val endDateTime = if (isAllDay) {
                                selectedDate.plusDays(1).atStartOfDay()
                            } else {
                                LocalDateTime.of(
                                    selectedDate,
                                    LocalTime.parse(endTime, timeFormatter)
                                )
                            }

                            if (!endDateTime.isAfter(startDateTime)) {
                                error = "End time must be after start time."
                                return@TextButton
                            }

                            val startMillis = startDateTime
                                .atZone(zone)
                                .toInstant()
                                .toEpochMilli()

                            val endMillis = endDateTime
                                .atZone(zone)
                                .toInstant()
                                .toEpochMilli()

                            onSave(
                                CalendarEventEntity(
                                    id = existingEvent?.id ?: 0,
                                    title = title.trim(),
                                    description = description.trim(),
                                    startTime = startMillis,
                                    endTime = endMillis,
                                    location = location.trim(),
                                    isAllDay = isAllDay,
                                    createdAt = existingEvent?.createdAt
                                        ?: System.currentTimeMillis()
                                )
                            )

                        } catch (exception: Exception) {
                            error = "Enter valid times in HH:mm format, e.g. 14:30."
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Delete confirmation
    if (showDeleteConfirmation && existingEvent != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },

            title = {
                Text("Delete Event?")
            },

            text = {
                Text(
                    "Are you sure you want to delete \"${existingEvent.title}\"? This action cannot be undone."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete(existingEvent)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Keep Event")
                }
            }
        )
    }
}
