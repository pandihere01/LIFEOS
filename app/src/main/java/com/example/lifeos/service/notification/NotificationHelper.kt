
package com.example.lifeos.service.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lifeos.MainActivity
import com.example.lifeos.R

object NotificationHelper {

    // New channel ID because Android preserves old channel settings.
    private const val CHANNEL_ID = "lifeos_reminders_v2"
    private const val CHANNEL_NAME = "LIFEOS Reminders"

    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri: Uri =
                Settings.System.DEFAULT_NOTIFICATION_URI

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for LIFEOS reminders"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(soundUri, audioAttributes)
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        description: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        createNotificationChannel(context)

        // Opens LIFEOS when the notification is tapped.
        val openAppIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(
                description.ifBlank { "Your reminder is due!" }
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        description.ifBlank { "Your reminder is due!" }
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notificationId, notification)
    }
}
