package com.paulcoding.hviewer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.paulcoding.hviewer.helper.CrashHandler
import com.paulcoding.hviewer.helper.setupPaths
import com.paulcoding.hviewer.worker.scheduleApkUpdate
import com.paulcoding.hviewer.worker.scheduleScriptsUpdate
import com.paulcoding.js.JS
import com.tencent.mmkv.MMKV

class MainApp : Application() {
    override fun onCreate() {
        CrashHandler.install()
        super.onCreate()
        appContext = this
        MMKV.initialize(this)
        JS.initialize(this)

        setupPaths()
        setupNotificationChannels()
        setupWorkers()
    }

    private fun setupWorkers() {
        scheduleScriptsUpdate(this)
        scheduleApkUpdate(this)
    }

    private fun setupNotificationChannels() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val checkForUpdateChannel = NotificationChannel(
            CHECK_FOR_UPDATE_CHANNEL,
            "Check for update",
            NotificationManager.IMPORTANCE_LOW
        )
        val checkForUpdateApkChannel = NotificationChannel(
            CHECK_FOR_UPDATE_APK_CHANNEL,
            "Check for update",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(checkForUpdateChannel)
        notificationManager.createNotificationChannel(checkForUpdateApkChannel)
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}