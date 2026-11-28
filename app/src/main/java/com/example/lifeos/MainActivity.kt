
package com.example.lifeos

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider

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

import com.example.lifeos.ui.habits.HabitScreen
import com.example.lifeos.ui.habits.HabitViewModel
import com.example.lifeos.ui.habits.HabitViewModelFactory

import com.example.lifeos.ui.goals.GoalViewModel
import com.example.lifeos.ui.goals.GoalViewModelFactory

import com.example.lifeos.ui.theme.LIFEOSTheme

import com.example.lifeos.ui.goals.GoalScreen


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

        val container = (application as LifeOSApplication).container

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

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        // 1. Initialize Room database
        val database = LifeOSDatabase.getDatabase(applicationContext)

        // 2. Initialize repositories
        val taskRepository = TaskRepository(
            taskDao = database.taskDao(),
            weeklyTaskDao = database.weeklyTaskDao(),
            weeklyTaskCompletionDao = database.weeklyTaskCompletionDao()
        )

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

        val habitViewModel = ViewModelProvider(
            this,
            HabitViewModelFactory(container.habitRepository)
        )[HabitViewModel::class.java]

        val goalViewModel = ViewModelProvider(
            this,
            GoalViewModelFactory(container.goalRepository)
        )[GoalViewModel::class.java]

        // Tracks the previous Back press on Dashboard
        var lastBackPressedTime = 0L

        // 5. Set up application UI
        setContent {
            var currentScreen by remember {
                mutableStateOf("dashboard")
            }

            // Handle system Back button
            BackHandler {
                if (currentScreen != "dashboard") {
                    // Return to Dashboard from any feature screen
                    currentScreen = "dashboard"
                    lastBackPressedTime = 0L
                } else {
                    val currentTime = SystemClock.elapsedRealtime()

                    if (currentTime - lastBackPressedTime < 2000L) {
                        // Second Back press: exit application
                        finish()
                    } else {
                        // First Back press: show message
                        lastBackPressedTime = currentTime

                        Toast.makeText(
                            this@MainActivity,
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
                            },
                            onHabitsClick = {
                                currentScreen = "habits"
                            } ,
                            onGoalsClick = {
                                currentScreen = "goals"
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

                    "habits" -> {
                        HabitScreen(
                            onBack = {
                                currentScreen = "dashboard"
                            },
                            habitViewModel = habitViewModel
                        )
                    }

                    "goals" -> {
                        GoalScreen(
                            onBack = {
                                currentScreen = "dashboard"
                            },
                            goalViewModel = goalViewModel,
                            habitViewModel = habitViewModel
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
