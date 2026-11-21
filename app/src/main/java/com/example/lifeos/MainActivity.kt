package com.example.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lifeos.ui.dashboard.DashboardScreen
import com.example.lifeos.ui.tasks.TasksScreen
import com.example.lifeos.ui.theme.LIFEOSTheme

import androidx.compose.runtime.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            /*
            LIFEOSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

             */

            /*LIFEOSTheme  {
                DashboardScreen()
            }

             */

            var showTasks by remember {
                mutableStateOf(false)
            }

            if (showTasks) {
                TasksScreen(
                    onBack = {
                        showTasks = false
                    }
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
