
package com.example.lifeos.ui.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeos.data.local.entity.ReminderEntity
import com.example.lifeos.ui.components.ReminderDialog
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// LIFEOS theme colors
private val ReminderBackground = Color(0xFF0B1020)
private val ReminderNavy = Color(0xFF171F33)
private val ReminderAccent = Color(0xFF36BFFA)
private val ReminderTextPrimary = Color.White
private val ReminderTextSecondary = Color(0xFFB0BECF)

@Composable
fun ReminderScreen(
    onBack: () -> Unit,
    reminderViewModel: ReminderViewModel
) {
    val reminders by reminderViewModel.reminders
        .collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var editingReminder by remember {
        mutableStateOf<ReminderEntity?>(null)
    }

    Scaffold(
        containerColor = ReminderBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // HEADER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ReminderNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = ReminderTextPrimary
                        )
                    ) {
                        Text("Back")
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "LIFEOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ReminderAccent
                        )

                        Text(
                            text = "Reminders",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = ReminderTextPrimary
                        )
                    }
                }
            }

            // MAIN CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ReminderBackground)
                    .padding(16.dp)
            ) {

                // SECTION TITLE + ADD BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ReminderTextPrimary
                    )

                    Button(
                        onClick = {
                            editingReminder = null
                            showDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ReminderAccent,
                            contentColor = ReminderBackground
                        )
                    ) {
                        Text("+ Add", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // EMPTY STATE
                if (reminders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No reminders yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ReminderTextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Create your first reminder!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ReminderTextSecondary
                            )
                        }
                    }
                } else {

                    // REMINDER LIST
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = reminders,
                            key = { it.id }
                        ) { reminder ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = ReminderNavy
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Checkbox(
                                        checked = reminder.isCompleted,
                                        onCheckedChange = { checked ->
                                            reminderViewModel.updateReminder(
                                                reminder.copy(
                                                    isCompleted = checked
                                                )
                                            )
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = ReminderAccent,
                                            uncheckedColor = ReminderTextSecondary,
                                            checkmarkColor = ReminderBackground
                                        )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    ) {

                                        Text(
                                            text = reminder.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (reminder.isCompleted)
                                                ReminderTextSecondary
                                            else
                                                ReminderTextPrimary
                                        )

                                        if (reminder.description.isNotBlank()) {
                                            Spacer(
                                                modifier = Modifier.height(4.dp)
                                            )

                                            Text(
                                                text = reminder.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ReminderTextSecondary
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier.height(6.dp)
                                        )

                                        val formattedTime = Instant
                                            .ofEpochMilli(reminder.reminderTime)
                                            .atZone(ZoneId.systemDefault())
                                            .format(
                                                DateTimeFormatter.ofPattern(
                                                    "dd MMM yyyy, hh:mm a",
                                                    Locale.getDefault()
                                                )
                                            )

                                        Text(
                                            text = formattedTime,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ReminderAccent
                                        )

                                        Spacer(
                                            modifier = Modifier.height(5.dp)
                                        )

                                        Text(
                                            text = "Priority: ${reminder.priority} · Repeat: ${reminder.recurrenceType}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ReminderTextSecondary
                                        )
                                    }

                                    // EDIT
                                    IconButton(
                                        onClick = {
                                            editingReminder = reminder
                                            showDialog = true
                                        }
                                    ) {
                                        Text(
                                            text = "Edit",
                                            color = ReminderAccent,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }

                                    // DELETE
                                    IconButton(
                                        onClick = {
                                            reminderViewModel.deleteReminder(reminder)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete reminder",
                                            tint = Color(0xFFFF6B6B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // REMINDER DIALOG
    if (showDialog) {
        ReminderDialog(
            existingReminder = editingReminder,

            onDismiss = {
                showDialog = false
                editingReminder = null
            },

            onSave = { reminder ->
                if (editingReminder == null) {
                    reminderViewModel.addReminder(reminder)
                } else {
                    reminderViewModel.updateReminder(reminder)
                }

                showDialog = false
                editingReminder = null
            }
        )
    }
}
