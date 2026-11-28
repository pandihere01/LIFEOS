
package com.example.lifeos.service.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.lifeos.MainActivity

class TaskSummaryNotificationHelper(
    private val context: Context
) {

    companion object {
        private const val CHANNEL_ID = "lifeos_daily_task_summary"
        private const val NOTIFICATION_ID = 6001
    }

    fun showDailySummary(taskTitles: List<String>) {
        createNotificationChannel()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val contentIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title: String
        val message: String

        if (taskTitles.isEmpty()) {
            title = "Good morning!"
            message = "You have no pending tasks for today."
        } else {
            title = "Your plan for today"
            message = "You have ${taskTitles.size} pending task(s)."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.InboxStyle().also { style ->
                    taskTitles.take(5).forEach { taskTitle ->
                        style.addLine("• $taskTitle")
                    }

                    if (taskTitles.size > 5) {
                        style.addLine("+ ${taskTitles.size - 5} more task(s)")
                    }
                }
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Notification permission may have been revoked.
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Task Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Your pending tasks every morning at 6:00 AM"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}
