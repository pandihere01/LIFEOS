
package com.example.lifeos.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.lifeos.data.local.LifeOSDatabase
import com.example.lifeos.data.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_TYPE = "alarm_type"

        const val TYPE_ADVANCE = "ADVANCE"
        const val TYPE_MAIN = "MAIN"
        const val TYPE_SNOOZE = "SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val reminderId = intent.getIntExtra("reminder_id", -1)
        if (reminderId == -1) return

        val title = intent.getStringExtra("reminder_title")
            ?: "LIFEOS Reminder"

        val description = intent.getStringExtra("reminder_description")
            ?: "Your reminder is due!"

        when (intent.getStringExtra(EXTRA_ALARM_TYPE)) {

            TYPE_ADVANCE -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = reminderId + 300000,
                    title = "Reminder in 15 minutes: $title",
                    description = description
                )
            }

            TYPE_MAIN, TYPE_SNOOZE -> {

                val serviceIntent = Intent(
                    context,
                    AlarmRingingService::class.java
                ).apply {
                    action = AlarmRingingService.ACTION_START

                    putExtra(
                        AlarmRingingService.EXTRA_REMINDER_ID,
                        reminderId
                    )

                    putExtra(
                        AlarmRingingService.EXTRA_TITLE,
                        title
                    )

                    putExtra(
                        AlarmRingingService.EXTRA_DESCRIPTION,
                        description
                    )
                }

                ContextCompat.startForegroundService(
                    context,
                    serviceIntent
                )

                // Only the original scheduled alarm advances recurrence.
                if (intent.getStringExtra(EXTRA_ALARM_TYPE) == TYPE_MAIN) {
                    scheduleNextOccurrence(context, reminderId)
                }
            }
        }
    }

    private fun scheduleNextOccurrence(
        context: Context,
        reminderId: Int
    ) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = LifeOSDatabase.getDatabase(
                    context.applicationContext
                )

                val repository = ReminderRepository(
                    database.reminderDao()
                )

                val reminder = repository.getReminderById(reminderId)
                    ?: return@launch

                if (
                    reminder.isCompleted ||
                    !reminder.isRecurring ||
                    !reminder.isNotificationEnabled
                ) {
                    return@launch
                }

                val zone = ZoneId.systemDefault()

                val currentDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(reminder.reminderTime),
                    zone
                )

                val nextDateTime = when (reminder.recurrenceType) {
                    "DAILY" -> currentDateTime.plusDays(1)
                    "WEEKLY" -> currentDateTime.plusWeeks(1)
                    "MONTHLY" -> currentDateTime.plusMonths(1)
                    else -> return@launch
                }

                val nextTime = nextDateTime
                    .atZone(zone)
                    .toInstant()
                    .toEpochMilli()

                val updatedReminder = reminder.copy(
                    reminderTime = nextTime
                )

                repository.updateReminder(updatedReminder)

                ReminderScheduler(context.applicationContext)
                    .scheduleReminder(
                        reminderId = reminder.id,
                        title = reminder.title,
                        description = reminder.description,
                        reminderTime = nextTime
                    )

            } finally {
                pendingResult.finish()
            }
        }
    }
}
