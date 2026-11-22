
package com.example.lifeos.service.notification

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import android.util.Log

import android.os.Handler
import android.os.Looper

class AlarmRingingService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    private val alarmHandler = Handler(Looper.getMainLooper())

    private val stopAlarmRunnable = Runnable {
        stopAlarmSound()
    }

    companion object {
        const val ACTION_START = "com.example.lifeos.ACTION_START_ALARM"
        const val ACTION_SNOOZE = "com.example.lifeos.ACTION_SNOOZE_ALARM"
        const val ACTION_DISMISS = "com.example.lifeos.ACTION_DISMISS_ALARM"

        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_TITLE = "reminder_title"
        const val EXTRA_DESCRIPTION = "reminder_description"

        private const val NOTIFICATION_ID = 9001
        private const val TAG = "AlarmRingingService"
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START -> {
                val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
                val title = intent.getStringExtra(EXTRA_TITLE)
                    ?: "LIFEOS Alarm"
                val description = intent.getStringExtra(EXTRA_DESCRIPTION)
                    ?: "Your reminder is due!"

                if (reminderId == -1) {
                    stopSelf()
                    return START_NOT_STICKY
                }

                startForeground(
                    NOTIFICATION_ID,
                    AlarmNotificationHelper.createAlarmNotification(
                        this,
                        reminderId,
                        title,
                        description
                    )
                )

                startAlarmSound()
            }

            ACTION_SNOOZE -> {
                val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
                val title = intent.getStringExtra(EXTRA_TITLE)
                    ?: "LIFEOS Alarm"
                val description = intent.getStringExtra(EXTRA_DESCRIPTION)
                    ?: "Your reminder is due!"

                if (reminderId != -1) {
                    ReminderScheduler(this).scheduleSnooze(
                        reminderId = reminderId,
                        title = title,
                        description = description
                    )
                }

                stopAlarmSound()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            ACTION_DISMISS -> {
                stopAlarmSound()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun startAlarmSound() {
        stopAlarmSound()

        try {
            val alarmUri: Uri =
                Settings.System.DEFAULT_ALARM_ALERT_URI

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(
                            AudioAttributes.CONTENT_TYPE_SONIFICATION
                        )
                        .build()
                )

                setDataSource(this@AlarmRingingService, alarmUri)
                isLooping = true
                prepare()
                start()
            }

            // Automatically stop ringing after 1 minute.
            alarmHandler.postDelayed(
                stopAlarmRunnable,
                60_000L
            )

        } catch (exception: Exception) {
            Log.e(TAG, "Unable to start alarm sound", exception)
            stopAlarmSound()
        }
    }

    private fun stopAlarmSound() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (exception: Exception) {
                Log.e(TAG, "Unable to stop alarm sound", exception)
            }
        }

        mediaPlayer = null
    }

    override fun onDestroy() {
        stopAlarmSound()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
