
package com.example.lifeos.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeos.data.local.entity.WeeklyTaskEntity
import com.example.lifeos.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val weekDays = listOf(
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday"
)

@Composable
fun TasksScreen(
    onBack: () -> Unit,
    taskViewModel: TaskViewModel
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val weeklyTasks by taskViewModel.weeklyTasks.collectAsState()
    val weeklyCompletions by taskViewModel.weeklyCompletions.collectAsState()
    val selectedDateString by taskViewModel.selectedDate.collectAsState()

    val selectedDate = remember(selectedDateString) {
        LocalDate.parse(selectedDateString)
    }

    var showDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var isWeeklyTask by remember { mutableStateOf(false) }
    var selectedDayOfWeek by remember(selectedDateString) {
        mutableIntStateOf(selectedDate.dayOfWeek.value)
    }
    var editingWeeklyTask by remember {
        mutableStateOf<WeeklyTaskEntity?>(null)
    }

    var expandedDayMenu by remember { mutableStateOf(false) }

    val completedWeeklyIds = weeklyCompletions
        .filter { it.isCompleted }
        .map { it.weeklyTaskId }
        .toSet()

    val completedCount =
        tasks.count { it.isCompleted } + completedWeeklyIds.size

    val pendingCount =
        tasks.count { !it.isCompleted } +
                weeklyTasks.count { it.id !in completedWeeklyIds }

    val totalCount = tasks.size + weeklyTasks.size

    val progress = if (totalCount > 0) {
        completedCount.toFloat() / totalCount
    } else {
        0f
    }

    val dateFormatter = DateTimeFormatter.ofPattern(
        "EEEE, dd MMMM yyyy",
        Locale.getDefault()
    )

    fun closeDialog() {
        showDialog = false
        taskTitle = ""
        isWeeklyTask = false
        editingWeeklyTask = null
        selectedDayOfWeek = selectedDate.dayOfWeek.value
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LifeOSBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {

            TextButton(onClick = onBack) {
                Text(
                    "← Back to LIFEOS",
                    color = LifeOSPurple,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "My Tasks",
                color = LifeOSText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Plan your day. Get things done.",
                color = LifeOSMuted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            // Date selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LifeOSSurface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                taskViewModel.selectDate(
                                    selectedDate.minusDays(1).toString()
                                )
                            }
                        ) {
                            Text(
                                "‹",
                                color = LifeOSPurple,
                                fontSize = 30.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                when (selectedDate) {
                                    LocalDate.now() -> "TODAY"
                                    LocalDate.now().plusDays(1) -> "TOMORROW"
                                    LocalDate.now().minusDays(1) -> "YESTERDAY"
                                    else -> "SELECTED DAY"
                                },
                                color = LifeOSPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                selectedDate.format(dateFormatter),
                                color = LifeOSText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                taskViewModel.selectDate(
                                    selectedDate.plusDays(1).toString()
                                )
                            }
                        ) {
                            Text(
                                "›",
                                color = LifeOSPurple,
                                fontSize = 30.sp
                            )
                        }
                    }

                    if (selectedDate != LocalDate.now()) {
                        TextButton(
                            onClick = { taskViewModel.selectToday() }
                        ) {
                            Text(
                                "Go to Today",
                                color = LifeOSPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Daily summary
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TaskSummary(
                    title = "Pending",
                    count = pendingCount,
                    modifier = Modifier.weight(1f),
                    countColor = LifeOSPurple
                )

                TaskSummary(
                    title = "Completed",
                    count = completedCount,
                    modifier = Modifier.weight(1f),
                    countColor = LifeOSGreen
                )
            }

            Spacer(Modifier.height(14.dp))

            // Daily progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LifeOSSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Daily Progress",
                            color = LifeOSText,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            "$completedCount / $totalCount completed",
                            color = LifeOSPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = LifeOSGreen,
                        trackColor = LifeOSBackground
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Combined task list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                item {
                    Text(
                        "WEEKLY ROUTINE",
                        color = LifeOSText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        "${weekDays[selectedDate.dayOfWeek.value - 1]} recurring tasks",
                        color = LifeOSMuted,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(6.dp))
                }

                if (weeklyTasks.isEmpty()) {
                    item {
                        Text(
                            "No recurring tasks for this day.",
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = LifeOSMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                items(
                    items = weeklyTasks,
                    key = { "weekly-${it.id}" }
                ) { task ->

                    val isCompleted = task.id in completedWeeklyIds

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = LifeOSSurface
                        ),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = { checked ->
                                    taskViewModel.updateWeeklyTaskCompletion(
                                        task,
                                        checked
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LifeOSGreen,
                                    uncheckedColor = LifeOSMuted,
                                    checkmarkColor = LifeOSSurface
                                )
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    task.title,
                                    color = if (isCompleted) {
                                        LifeOSMuted
                                    } else {
                                        LifeOSText
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    "Repeats every ${weekDays[task.dayOfWeek - 1]}",
                                    color = LifeOSMuted,
                                    fontSize = 11.sp
                                )
                            }

                            TextButton(
                                onClick = {
                                    editingWeeklyTask = task
                                    taskTitle = task.title
                                    isWeeklyTask = true
                                    selectedDayOfWeek = task.dayOfWeek
                                    showDialog = true
                                }
                            ) {
                                Text(
                                    "Edit",
                                    color = LifeOSPurple,
                                    fontSize = 12.sp
                                )
                            }

                            TextButton(
                                onClick = {
                                    taskViewModel.deleteWeeklyTask(task)
                                }
                            ) {
                                Text(
                                    "Delete",
                                    color = LifeOSRed,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        "ONE-TIME TASKS",
                        color = LifeOSText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        "Tasks planned specifically for this date",
                        color = LifeOSMuted,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(6.dp))
                }

                if (tasks.isEmpty()) {
                    item {
                        Text(
                            "No one-time tasks planned for this day.",
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = LifeOSMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                items(
                    items = tasks,
                    key = { "task-${it.id}" }
                ) { task ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = LifeOSSurface
                        ),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { checked ->
                                    taskViewModel.updateTask(task, checked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LifeOSGreen,
                                    uncheckedColor = LifeOSMuted,
                                    checkmarkColor = LifeOSSurface
                                )
                            )

                            Text(
                                text = task.title,
                                modifier = Modifier.weight(1f),
                                color = if (task.isCompleted) {
                                    LifeOSMuted
                                } else {
                                    LifeOSText
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            TextButton(
                                onClick = {
                                    taskViewModel.deleteTask(task)
                                }
                            ) {
                                Text(
                                    "Delete",
                                    color = LifeOSRed,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    closeDialog()
                    showDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LifeOSPurple,
                    contentColor = LifeOSSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "+ Add New Task",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }

    // Add / Edit task dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { closeDialog() },

            title = {
                Text(
                    if (editingWeeklyTask != null) {
                        "Edit Weekly Task"
                    } else {
                        "Create a task"
                    },
                    color = LifeOSText,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Column {

                    if (editingWeeklyTask == null) {
                        Text(
                            "Task type",
                            color = LifeOSText,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !isWeeklyTask,
                                onClick = { isWeeklyTask = false },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = LifeOSPurple
                                )
                            )

                            Text(
                                "One-time",
                                color = LifeOSText,
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.width(8.dp))

                            RadioButton(
                                selected = isWeeklyTask,
                                onClick = { isWeeklyTask = true },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = LifeOSPurple
                                )
                            )

                            Text(
                                "Weekly",
                                color = LifeOSText,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Task name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LifeOSPurple,
                            focusedLabelColor = LifeOSPurple,
                            cursorColor = LifeOSPurple
                        )
                    )

                    if (isWeeklyTask) {
                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Repeat every",
                            color = LifeOSText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )

                        Box {
                            OutlinedButton(
                                onClick = {
                                    expandedDayMenu = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = LifeOSPurple
                                )
                            ) {
                                Text(weekDays[selectedDayOfWeek - 1])
                            }

                            DropdownMenu(
                                expanded = expandedDayMenu,
                                onDismissRequest = {
                                    expandedDayMenu = false
                                }
                            ) {
                                weekDays.forEachIndexed { index, day ->
                                    DropdownMenuItem(
                                        text = { Text(day) },
                                        onClick = {
                                            selectedDayOfWeek = index + 1
                                            expandedDayMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Scheduled for: ${selectedDate.format(dateFormatter)}",
                            color = LifeOSMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        val title = taskTitle.trim()

                        if (title.isNotEmpty()) {
                            val editingTask = editingWeeklyTask

                            if (editingTask != null) {
                                taskViewModel.updateWeeklyTask(
                                    editingTask.copy(
                                        title = title,
                                        dayOfWeek = selectedDayOfWeek
                                    )
                                )
                            } else if (isWeeklyTask) {
                                taskViewModel.addWeeklyTask(
                                    title = title,
                                    dayOfWeek = selectedDayOfWeek
                                )
                            } else {
                                taskViewModel.addTask(title)
                            }

                            closeDialog()
                        }
                    }
                ) {
                    Text(
                        if (editingWeeklyTask != null) "Save" else "Add",
                        color = LifeOSPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { closeDialog() }
                ) {
                    Text(
                        "Cancel",
                        color = LifeOSMuted
                    )
                }
            },

            containerColor = LifeOSSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun TaskSummary(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    countColor: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LifeOSSurface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                color = LifeOSMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(5.dp))

            Text(
                count.toString(),
                color = countColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
