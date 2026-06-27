package com.lifetracker

import android.app.Application
import com.lifetracker.data.AppDatabase
import com.lifetracker.notification.NotificationHelper

class LifeTrackerApp : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
