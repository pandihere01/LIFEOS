
package com.example.lifeos.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lifeos.MainActivity
import com.example.lifeos.R

object AlarmNotificationHelper {

    private const val CHANNEL_ID = "lifeos_active_alarm"
    private const val CHANNEL_NAME = "LIFEOS Active Alarms"

    fun createAlarmNotification(
        context: Context,
        reminderId: Int,
        title: String,
        description: String
    ): Notification {

        createChannel(context)

        val openIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val openPendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePendingIntent = createActionPendingIntent(
            context,
            reminderId + 100000,
            AlarmRingingService.ACTION_SNOOZE,
            reminderId,
            title,
            description
        )

        val dismissPendingIntent = createActionPendingIntent(
            context,
            reminderId + 200000,
            AlarmRingingService.ACTION_DISMISS,
            reminderId,
            title,
            description
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⏰ $title")
            .setContentText(
                description.ifBlank { "Your reminder is ringing!" }
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description.ifBlank {
                        "Your reminder is ringing!"
                    })
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(openPendingIntent)
            .addAction(
                R.mipmap.ic_launcher,
                "Snooze 5 min",
                snoozePendingIntent
            )
            .addAction(
                R.mipmap.ic_launcher,
                "Dismiss",
                dismissPendingIntent
            )
            .build()
    }

    private fun createActionPendingIntent(
        context: Context,
        requestCode: Int,
        action: String,
        reminderId: Int,
        title: String,
        description: String
    ): PendingIntent {

        val intent = Intent(
            context,
            AlarmRingingService::class.java
        ).apply {
            this.action = action

            putExtra(AlarmRingingService.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmRingingService.EXTRA_TITLE, title)
            putExtra(AlarmRingingService.EXTRA_DESCRIPTION, description)
        }

        return PendingIntent.getForegroundService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Continuous LIFEOS alarm notifications"
                setSound(null, null)
                enableVibration(true)
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}
