
package com.example.lifeos.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeos.ui.theme.*

@Composable
fun TasksScreen(
    onBack: () -> Unit,
    taskViewModel: TaskViewModel
) {
    // Observe tasks from Room through ViewModel
    val tasks by taskViewModel.tasks.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }

    val pendingCount = tasks.count { !it.isCompleted }
    val completedCount = tasks.count { it.isCompleted }

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

            // Back navigation
            TextButton(onClick = onBack) {
                Text(
                    "← Back to LIFEOS",
                    color = LifeOSPurple,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            // Header
            Text(
                "My Tasks",
                color = LifeOSText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Stay organized. Get things done.",
                color = LifeOSMuted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(24.dp))

            // Task summary cards
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

            Spacer(Modifier.height(26.dp))

            Text(
                "YOUR TASK LIST",
                color = LifeOSText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(12.dp))

            // Empty state or task list
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks yet.\nTap + to create your first task.",
                        color = LifeOSMuted,
                        fontSize = 15.sp,
                        lineHeight = 23.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = LifeOSSurface
                            ),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
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
                                        taskViewModel.updateTask(
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

                                Text(
                                    text = task.title,
                                    modifier = Modifier.weight(1f),
                                    color = if (task.isCompleted)
                                        LifeOSMuted
                                    else
                                        LifeOSText,
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
                }
            }

            // Add task button
            Button(
                onClick = { showDialog = true },
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

    // Create task dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                taskTitle = ""
            },
            title = {
                Text(
                    "Create a task",
                    color = LifeOSText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LifeOSPurple,
                        focusedLabelColor = LifeOSPurple,
                        cursorColor = LifeOSPurple
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val title = taskTitle.trim()

                        if (title.isNotEmpty()) {
                            taskViewModel.addTask(title)
                            taskTitle = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text(
                        "Add",
                        color = LifeOSPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        taskTitle = ""
                    }
                ) {
                    Text("Cancel", color = LifeOSMuted)
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
    countColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LifeOSSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
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
