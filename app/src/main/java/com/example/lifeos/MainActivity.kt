
package com.example.lifeos


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings

import com.example.lifeos.data.local.LifeOSDatabase
import com.example.lifeos.data.repository.TaskRepository
import com.example.lifeos.data.repository.CalendarRepository
import com.example.lifeos.data.repository.ReminderRepository

import com.example.lifeos.service.notification.ReminderScheduler

import com.example.lifeos.ui.dashboard.DashboardScreen

import com.example.lifeos.ui.tasks.TasksScreen
import com.example.lifeos.ui.tasks.TaskViewModel
import com.example.lifeos.ui.tasks.TaskViewModelFactory

import com.example.lifeos.ui.calender.CalendarScreen
import com.example.lifeos.ui.calender.CalendarViewModel
import com.example.lifeos.ui.calender.CalendarViewModelFactory

import com.example.lifeos.ui.reminders.ReminderScreen
import com.example.lifeos.ui.reminders.ReminderViewModel
import com.example.lifeos.ui.reminders.ReminderViewModelFactory

import com.example.lifeos.ui.theme.LIFEOSTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Notification permission granted
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        requestExactAlarmPermission()

        enableEdgeToEdge()

        // 1. Initialize Room database
        val database = LifeOSDatabase.getDatabase(applicationContext)

        // 2. Initialize repositories
        val taskRepository = TaskRepository(database.taskDao())

        val calendarRepository =
            CalendarRepository(database.calendarEventDao())

        val reminderRepository =
            ReminderRepository(database.reminderDao())

        // 3. Initialize reminder scheduler
        val reminderScheduler = ReminderScheduler(applicationContext)

        // 4. Initialize ViewModels
        val taskViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(taskRepository)
        )[TaskViewModel::class.java]

        val calendarViewModel = ViewModelProvider(
            this,
            CalendarViewModelFactory(calendarRepository)
        )[CalendarViewModel::class.java]

        val reminderViewModel = ViewModelProvider(
            this,
            ReminderViewModelFactory(
                reminderRepository,
                reminderScheduler
            )
        )[ReminderViewModel::class.java]

        // 5. Set up application UI
        setContent {
            var currentScreen by remember {
                mutableStateOf("dashboard")
            }

            LIFEOSTheme {
                when (currentScreen) {

                    "dashboard" -> {
                        DashboardScreen(
                            onTasksClick = {
                                currentScreen = "tasks"
                            },
                            onCalendarClick = {
                                currentScreen = "calendar"
                            },
                            onRemindersClick = {
                                currentScreen = "reminders"
                            }
                        )
                    }

                    "tasks" -> {
                        TasksScreen(
                            onBack = {
                                currentScreen = "dashboard"
                            },
                            taskViewModel = taskViewModel
                        )
                    }

                    "calendar" -> {
                        CalendarScreen(
                            onBack = {
                                currentScreen = "dashboard"
                            },
                            calendarViewModel = calendarViewModel
                        )
                    }

                    "reminders" -> {
                        ReminderScreen(
                            onBack = {
                                currentScreen = "dashboard"
                            },
                            reminderViewModel = reminderViewModel
                        )
                    }
                }
            }
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val alarmManager =
                getSystemService(ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {

                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:$packageName")
                )

                startActivity(intent)
            }
        }
    }
}
