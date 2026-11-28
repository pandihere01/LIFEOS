
package com.example.lifeos.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.lifeos.data.local.LifeOSDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskSummaryReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "LIFEOS_TaskSummary"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Schedule tomorrow's notification first.
                TaskSummaryScheduler(appContext).scheduleNextSummary()

                val database = LifeOSDatabase.getDatabase(appContext)

                val currentDate = LocalDate.now()
                val today = currentDate.toString()
                val dayOfWeek = currentDate.dayOfWeek.value

                // Retrieve pending one-time tasks.
                val pendingTasks =
                    database.taskDao().getPendingTasksForDate(today)

                // Retrieve active weekly tasks assigned to today.
                val weeklyTasks =
                    database.weeklyTaskDao()
                        .getWeeklyTasksForDate(dayOfWeek, today)
                        .first()

                // Retrieve today's weekly task completion records.
                val weeklyCompletions =
                    database.weeklyTaskCompletionDao()
                        .getCompletionsForDate(today)
                        .first()

                // Exclude weekly tasks already completed today.
                val completedWeeklyTaskIds = weeklyCompletions
                    .filter { it.isCompleted }
                    .map { it.weeklyTaskId }
                    .toSet()

                val pendingWeeklyTasks = weeklyTasks.filter { task ->
                    task.id !in completedWeeklyTaskIds
                }

                // Combine both task types into one notification.
                val allPendingTaskTitles =
                    pendingTasks.map { it.title } +
                            pendingWeeklyTasks.map { it.title }

                Log.i(
                    TAG,
                    "Today's pending one-time tasks: ${pendingTasks.size}"
                )

                Log.i(
                    TAG,
                    "Today's pending weekly tasks: ${pendingWeeklyTasks.size}"
                )

                Log.i(
                    TAG,
                    "Total pending tasks: ${allPendingTaskTitles.size}"
                )

                TaskSummaryNotificationHelper(appContext)
                    .showDailySummary(allPendingTaskTitles)

            } catch (exception: Exception) {
                Log.e(TAG, "Failed to show daily task summary", exception)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
