
package com.example.lifeos.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.lifeos.data.local.LifeOSDatabase
import com.example.lifeos.data.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "LIFEOS_BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.i(TAG, "BOOT_COMPLETED received")

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appContext = context.applicationContext

                Log.i(TAG, "Initializing database")

                val database = LifeOSDatabase.getDatabase(appContext)
                val repository = ReminderRepository(database.reminderDao())
                val scheduler = ReminderScheduler(appContext)

                val currentTime = System.currentTimeMillis()
                val reminders = repository.getUpcomingReminders(currentTime)

                Log.i(TAG, "Upcoming reminders found: ${reminders.size}")

                reminders.forEach { reminder ->
                    Log.i(
                        TAG,
                        "Restoring reminder ID=${reminder.id}, time=${reminder.reminderTime}"
                    )

                    scheduler.scheduleReminder(
                        reminderId = reminder.id,
                        title = reminder.title,
                        description = reminder.description,
                        reminderTime = reminder.reminderTime
                    )
                }

                Log.i(TAG, "Alarm restoration completed")
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to restore alarms after reboot", exception)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
