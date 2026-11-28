package com.example.lifeos

import android.app.Application
import com.example.lifeos.di.LifeOSContainer
import com.example.lifeos.service.notification.TaskSummaryScheduler

class LifeOSApplication : Application() {

    lateinit var container: LifeOSContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = LifeOSContainer(this)

        // Schedule the daily task summary for 6:00 AM.
        TaskSummaryScheduler(this).scheduleNextSummary()
    }
}