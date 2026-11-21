
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
import com.example.lifeos.data.local.entity.TaskEntity

private val Background = Color(0xFF0B1020)
private val SurfaceDark = Color(0xFF171F33)
private val Accent = Color(0xFF36BFFA)

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
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {

            TextButton(onClick = onBack) {
                Text("← Back to LIFEOS", color = Accent)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "My Tasks",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Stay organized. Get things done.",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TaskSummary(
                    title = "Pending",
                    count = pendingCount,
                    modifier = Modifier.weight(1f)
                )

                TaskSummary(
                    title = "Completed",
                    count = completedCount,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "YOUR TASK LIST",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(12.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks yet.\nTap + to create your first task.",
                        color = Color.LightGray,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
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
                                        checkedColor = Accent
                                    )
                                )

                                Text(
                                    text = task.title,
                                    modifier = Modifier.weight(1f),
                                    color = if (task.isCompleted)
                                        Color.Gray else Color.White,
                                    fontSize = 15.sp
                                )

                                TextButton(
                                    onClick = {
                                        taskViewModel.deleteTask(task)
                                    }
                                ) {
                                    Text(
                                        "Delete",
                                        color = Color(0xFFFF7070)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "+ Add New Task",
                    color = Background,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                taskTitle = ""
            },
            title = {
                Text("Create a task")
            },
            text = {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task name") },
                    singleLine = true
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
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        taskTitle = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TaskSummary(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Text(
                count.toString(),
                color = Accent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
