package com.example.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lifeos.ui.dashboard.DashboardScreen
import com.example.lifeos.ui.tasks.TasksScreen
import com.example.lifeos.ui.theme.LIFEOSTheme

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.example.lifeos.data.local.LifeOSDatabase
import com.example.lifeos.data.repository.TaskRepository
import com.example.lifeos.ui.tasks.TaskViewModel
import com.example.lifeos.ui.tasks.TaskViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 1. Initialize Room database
        val database = LifeOSDatabase.getDatabase(applicationContext)

        // 2. Create repository
        val repository = TaskRepository(database.taskDao())

        // 3. Create ViewModel using the factory
        val taskViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(repository)
        )[TaskViewModel::class.java]

        // 4. Load the application UI
        setContent {

            var showTasks by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(false)
            }

            if (showTasks) {
                TasksScreen(
                    onBack = {
                        showTasks = false
                    },
                    taskViewModel = taskViewModel
                )
            } else {
                DashboardScreen(
                    onTasksClick = {
                        showTasks = true
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LIFEOSTheme {
        Greeting("Android")
    }
}
