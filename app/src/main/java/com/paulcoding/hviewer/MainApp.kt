package com.paulcoding.hviewer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.paulcoding.hviewer.di.appModule
import com.paulcoding.hviewer.di.repositoryModule
import com.paulcoding.hviewer.di.viewModelModule
import com.paulcoding.hviewer.helper.CrashHandler
import com.paulcoding.hviewer.helper.setupPaths
import com.paulcoding.hviewer.network.networkModule
import com.paulcoding.hviewer.worker.scheduleApkUpdate
import com.paulcoding.hviewer.worker.scheduleScriptsUpdate
import com.paulcoding.js.JS
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MainApp : Application() {
    override fun onCreate() {
        CrashHandler.install()
        super.onCreate()
        appContext = this
        dependencyInjection()
        MMKV.initialize(this)
        JS.initialize(this)

        setupPaths()
        setupNotificationChannels()
        setupWorkers()
    }

    private fun dependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(this@MainApp)
            workManagerFactory()
            modules(networkModule)
            modules(viewModelModule)
            modules(repositoryModule)
            modules(appModule)
        }
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