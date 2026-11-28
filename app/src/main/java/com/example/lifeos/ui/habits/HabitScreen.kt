
package com.example.lifeos.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeos.data.local.entity.HabitEntity
import com.example.lifeos.data.repository.HabitSchedule
import com.example.lifeos.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HabitScreen(
    onBack: () -> Unit,
    habitViewModel: HabitViewModel
) {
    val habits by habitViewModel.habits.collectAsStateWithLifecycle()
    val completions by habitViewModel.completionHistory.collectAsStateWithLifecycle()
    val streaks by habitViewModel.streaks.collectAsStateWithLifecycle()

    var showHabitDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }

    val today = LocalDate.now()
    val todayLabel = today.format(
        DateTimeFormatter.ofPattern("EEEE, dd MMMM")
    )

    val todaysHabits = habits.filter {
        HabitSchedule.isScheduledForDate(it, today)
    }

    val completedCount = todaysHabits.count {
        habitViewModel.isCompletedOnDate(it.id, today)
    }

    val progress = if (todaysHabits.isEmpty()) {
        0f
    } else {
        completedCount.toFloat() / todaysHabits.size
    }

    val bestCurrentStreak = streaks.values.maxOrNull() ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOSBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to home",
                            tint = LifeOSPurple
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LIFEOS",
                            color = LifeOSPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = "Your Habit Dashboard",
                            color = LifeOSText,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = todayLabel,
                            color = LifeOSMuted,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = LifeOSPurpleSoft,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌱", fontSize = 23.sp)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LifeOSSurface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Today's Progress",
                            color = LifeOSText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(112.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = LifeOSPurple,
                                    trackColor = LifeOSPurpleSoft,
                                    strokeWidth = 10.dp
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        color = LifeOSPurple,
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "completed",
                                        color = LifeOSMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(Modifier.width(22.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$completedCount of ${todaysHabits.size}",
                                    color = LifeOSText,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "habits completed",
                                    color = LifeOSMuted,
                                    fontSize = 13.sp
                                )

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    text = when {
                                        todaysHabits.isEmpty() ->
                                            "No habits scheduled today"

                                        completedCount == todaysHabits.size ->
                                            "Amazing! All done 🎉"

                                        completedCount > 0 ->
                                            "Keep going. You're doing great!"

                                        else ->
                                            "Let's make today count!"
                                    },
                                    color = LifeOSPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = LifeOSPurple,
                            trackColor = LifeOSPurpleSoft
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🔥",
                        value = "$bestCurrentStreak",
                        label = "Best active streak",
                        accent = LifeOSOrange
                    )

                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🎯",
                        value = "${habits.size}",
                        label = "Active habits",
                        accent = LifeOSPurple
                    )
                }
            }

            item {
                HabitHistorySection(
                    habits = habits,
                    completions = completions,
                    today = today
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Habits",
                            color = LifeOSText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Small steps, big changes.",
                            color = LifeOSMuted,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "${habits.size} habits",
                        color = LifeOSPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (habits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LifeOSSurface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌱", fontSize = 40.sp)

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "No habits yet",
                                color = LifeOSText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Start building your routine today.",
                                color = LifeOSMuted,
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    editingHabit = null
                                    showHabitDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LifeOSPurple
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Create first habit")
                            }
                        }
                    }
                }
            } else {
                items(
                    items = habits,
                    key = { it.id }
                ) { habit ->

                    val historyForHabit =
                        completions[habit.id].orEmpty()

                    val todayCompletion = historyForHabit.firstOrNull {
                        it.completionDate == today.toString()
                    }

                    val currentValue =
                        todayCompletion?.completedValue ?: 0.0

                    val scheduledToday =
                        HabitSchedule.isScheduledForDate(habit, today)

                    val completed =
                        habitViewModel.isCompletedOnDate(habit.id, today)

                    HabitItemCard(
                        habit = habit,
                        streak = streaks[habit.id] ?: 0,
                        completed = completed,
                        scheduledToday = scheduledToday,
                        scheduleLabel = getScheduleLabel(habit),
                        weeklyCount = habitViewModel.getWeeklyCompletionCount(
                            habit.id,
                            today
                        ),
                        weeklyTarget = HabitSchedule.getWeeklyTarget(habit),
                        currentValue = currentValue,
                        onToggle = {
                            if (completed) {
                                habitViewModel.undoCompletion(
                                    habitId = habit.id,
                                    date = today
                                )
                            } else if (scheduledToday) {
                                habitViewModel.recordCompletion(
                                    habitId = habit.id,
                                    date = today,
                                    value = 0.0
                                )
                            }
                        },
                        onUpdateProgress = { value ->
                            habitViewModel.recordCompletion(
                                habitId = habit.id,
                                date = today,
                                value = value
                            )
                        },
                        onEdit = {
                            editingHabit = habit
                            showHabitDialog = true
                        },
                        onDelete = {
                            habitToDelete = habit
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingHabit = null
                showHabitDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 22.dp, bottom = 20.dp),
            containerColor = LifeOSPurple,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add habit",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showHabitDialog) {
        AddHabitDialog(
            onDismiss = {
                showHabitDialog = false
                editingHabit = null
            },
            onSave = { habit ->
                if (habit.id == 0) {
                    habitViewModel.addHabit(habit)
                } else {
                    habitViewModel.updateHabit(habit)
                }

                showHabitDialog = false
                editingHabit = null
            },
            existingHabit = editingHabit
        )
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = {
                habitToDelete = null
            },
            title = {
                Text("Delete habit?")
            },
            text = {
                Text(
                    "Delete \"${habit.name}\" and its completion history? This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        habitViewModel.deleteHabit(habit)
                        habitToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        habitToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    label: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LifeOSSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(emoji, fontSize = 22.sp)

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                color = accent,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                color = LifeOSMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun HabitItemCard(
    habit: HabitEntity,
    streak: Int,
    completed: Boolean,
    scheduledToday: Boolean,
    scheduleLabel: String,
    weeklyCount: Int,
    weeklyTarget: Int,
    currentValue: Double,
    onToggle: () -> Unit,
    onUpdateProgress: (Double) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }

    val isMeasurable = habit.targetValue != null
    val target = habit.targetValue ?: 0.0
    val progressFraction = if (isMeasurable && target > 0) {
        (currentValue / target).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LifeOSSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isMeasurable) {
                        showProgressDialog = true
                    } else {
                        onToggle()
                    }
                },
                enabled = scheduledToday || completed,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (completed) {
                            LifeOSGreen
                        } else {
                            LifeOSPurpleSoft
                        },
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (completed) {
                        Icons.Default.Check
                    } else {
                        Icons.Default.Add
                    },
                    contentDescription = if (isMeasurable) {
                        "Update measurable progress"
                    } else if (completed) {
                        "Undo completion"
                    } else {
                        "Mark complete"
                    },
                    tint = if (completed) {
                        Color.White
                    } else {
                        LifeOSPurple
                    }
                )
            }

            Spacer(Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    color = LifeOSText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = habit.category,
                    color = LifeOSMuted,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = scheduleLabel,
                    color = if (scheduledToday) {
                        LifeOSPurple
                    } else {
                        LifeOSMuted
                    },
                    fontSize = 10.sp
                )

                if (isMeasurable) {
                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "${formatProgress(currentValue)} / ${formatProgress(target)} ${habit.unit}",
                        color = LifeOSPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(5.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = if (completed) LifeOSGreen else LifeOSPurple,
                        trackColor = LifeOSPurpleSoft
                    )

                    TextButton(
                        onClick = {
                            showProgressDialog = true
                        },
                        enabled = scheduledToday || completed,
                        contentPadding = PaddingValues(
                            horizontal = 0.dp,
                            vertical = 0.dp
                        ),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = if (completed) {
                                "Update progress"
                            } else {
                                "Add progress"
                            },
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = "Week: $weeklyCount / $weeklyTarget",
                    color = LifeOSPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                if (!scheduledToday) {
                    Text(
                        text = "Not scheduled today",
                        color = LifeOSMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔥 $streak",
                    color = LifeOSOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "streak",
                    color = LifeOSMuted,
                    fontSize = 10.sp
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Habit options",
                        tint = LifeOSMuted
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = {
                        showMenu = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }

    if (showProgressDialog && isMeasurable) {
        ProgressEntryDialog(
            habitName = habit.name,
            unit = habit.unit,
            currentValue = currentValue,
            target = target,
            onDismiss = {
                showProgressDialog = false
            },
            onSave = { value ->
                onUpdateProgress(value)
                showProgressDialog = false
            }
        )
    }
}

@Composable
private fun ProgressEntryDialog(
    habitName: String,
    unit: String,
    currentValue: Double,
    target: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var input by remember(currentValue) {
        mutableStateOf(
            if (currentValue == 0.0) "" else currentValue.toString()
        )
    }

    val enteredValue = input.toDoubleOrNull()
    val isValid = enteredValue != null &&
            enteredValue >= 0.0 &&
            enteredValue.isFinite()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Update progress")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = habitName,
                    color = LifeOSPurple,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Target: ${formatProgress(target)} $unit",
                    color = LifeOSMuted,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = {
                        Text("Total completed today")
                    },
                    suffix = {
                        Text(unit)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = input.isNotBlank() && !isValid,
                    supportingText = {
                        Text("Enter your total progress for today, not an additional amount.")
                    }
                )

                if (isValid) {
                    val percentage = if (target > 0) {
                        (enteredValue!! / target * 100).toInt()
                    } else {
                        0
                    }

                    Text(
                        text = if (enteredValue!! >= target) {
                            "Target reached! 🎉"
                        } else {
                            "$percentage% completed"
                        },
                        color = if (enteredValue >= target) {
                            LifeOSGreen
                        } else {
                            LifeOSPurple
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onSave(enteredValue!!)
                    }
                },
                enabled = isValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatProgress(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        "%.2f".format(java.util.Locale.US, value)
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun getScheduleLabel(habit: HabitEntity): String {
    return when (habit.frequency) {
        "DAILY" -> "Every day"

        "SELECTED_DAYS" -> {
            val dayNames = listOf(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
            )

            val selected = habit.selectedDays
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .filter { it in 1..7 }
                .sorted()

            if (selected.isEmpty()) {
                "No days selected"
            } else {
                selected.joinToString(", ") {
                    dayNames[it - 1]
                }
            }
        }

        "WEEKLY_TARGET" -> "${habit.targetPerWeek} days per week"

        else -> "Custom schedule"
    }
}
