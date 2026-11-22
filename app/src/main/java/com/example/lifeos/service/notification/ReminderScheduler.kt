
package com.example.lifeos.service.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class ReminderScheduler(context: Context) {

    private val appContext = context.applicationContext

    private val alarmManager =
        appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val FIFTEEN_MINUTES = 15 * 60 * 1000L
        private const val SNOOZE_DURATION = 5 * 60 * 1000L

        private const val REQUEST_ADVANCE_OFFSET = 100000
        private const val REQUEST_SNOOZE_OFFSET = 200000
    }

    fun scheduleReminder(
        reminderId: Int,
        title: String,
        description: String,
        reminderTime: Long
    ) {
        if (reminderTime <= System.currentTimeMillis()) return

        // Schedule notification 15 minutes before.
        val advanceTime = reminderTime - FIFTEEN_MINUTES

        if (advanceTime > System.currentTimeMillis()) {
            scheduleAlarm(
                reminderId = reminderId,
                title = title,
                description = description,
                alarmTime = advanceTime,
                alarmType = ReminderBroadcastReceiver.TYPE_ADVANCE,
                requestCode = reminderId + REQUEST_ADVANCE_OFFSET
            )
        }

        // Schedule continuous alarm at the exact time.
        scheduleAlarm(
            reminderId = reminderId,
            title = title,
            description = description,
            alarmTime = reminderTime,
            alarmType = ReminderBroadcastReceiver.TYPE_MAIN,
            requestCode = reminderId
        )
    }

    fun scheduleSnooze(
        reminderId: Int,
        title: String,
        description: String
    ) {
        val snoozeTime = System.currentTimeMillis() + SNOOZE_DURATION

        scheduleAlarm(
            reminderId = reminderId,
            title = title,
            description = description,
            alarmTime = snoozeTime,
            alarmType = ReminderBroadcastReceiver.TYPE_SNOOZE,
            requestCode = reminderId + REQUEST_SNOOZE_OFFSET
        )
    }

    private fun scheduleAlarm(
        reminderId: Int,
        title: String,
        description: String,
        alarmTime: Long,
        alarmType: String,
        requestCode: Int
    ) {

        val intent = Intent(
            appContext,
            ReminderBroadcastReceiver::class.java
        ).apply {
            action = "com.example.lifeos.REMINDER_$alarmType"

            putExtra("reminder_id", reminderId)
            putExtra("reminder_title", title)
            putExtra("reminder_description", description)

            putExtra(
                ReminderBroadcastReceiver.EXTRA_ALARM_TYPE,
                alarmType
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(reminderId: Int) {
        cancelAlarm(reminderId, ReminderBroadcastReceiver.TYPE_MAIN)
        cancelAlarm(
            reminderId + REQUEST_ADVANCE_OFFSET,
            ReminderBroadcastReceiver.TYPE_ADVANCE
        )
        cancelAlarm(
            reminderId + REQUEST_SNOOZE_OFFSET,
            ReminderBroadcastReceiver.TYPE_SNOOZE
        )
    }

    private fun cancelAlarm(
        requestCode: Int,
        alarmType: String
    ) {

        val intent = Intent(
            appContext,
            ReminderBroadcastReceiver::class.java
        ).apply {
            action = "com.example.lifeos.REMINDER_$alarmType"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
