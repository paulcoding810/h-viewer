package com.paulcoding.hviewer.worker

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.paulcoding.hviewer.ACTION_INSTALL_APK
import com.paulcoding.hviewer.APK_NAME
import com.paulcoding.hviewer.CHECK_FOR_UPDATE_APK_CHANNEL
import com.paulcoding.hviewer.MainActivity
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.Downloader
import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.ui.page.sites.post.DownloadService
import com.paulcoding.hviewer.ui.page.sites.post.DownloadService.Companion.ACTION_STOP_SERVICE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

class DownloadApkService(
    private val downloader: Downloader
) : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val job = SupervisorJob()
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val notificationId = 2

    private fun stopService() {
        job.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_STOP_SERVICE) {
            stopService()
            return START_NOT_STICKY
        }

        val release = intent.getParcelableExtra<HRelease>("release")

        if (release == null) {
            stopSelf()
            return START_NOT_STICKY
        } else {
            startForeground(notificationId, createNotification())
            downloadAndInstall(this, release)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }


    private fun downloadAndInstall(context: Context, release: HRelease) {
        try {
            CoroutineScope(coroutineContext).launch {
                val file = downloader.download(
                    downloadUrl = release.downloadUrl,
                    destination = File(context.cacheDir, APK_NAME)
                )
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                showDownloadCompleteNotification(release, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorNotification(e.message ?: "Unknown error")
            stopSelf()
        }
    }

    private fun showErrorNotification(msg: String) {
        notificationBuilder
            .setContentTitle(getString(R.string.error))
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, DownloadService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent =
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder =
            NotificationCompat.Builder(this, CHECK_FOR_UPDATE_APK_CHANNEL)
                .setContentTitle(this.getString(R.string.downloading_apk))
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(0, 0, true)
                .addAction(android.R.drawable.ic_delete, "Cancel", stopPendingIntent)
        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)
        return notification
    }


    private fun showDownloadCompleteNotification(release: HRelease, uri: Uri) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data = uri
            action = ACTION_INSTALL_APK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder
            .setContentTitle(getString(R.string.install_now))
            .setContentText(
                this.getString(
                    R.string.version_,
                    release.version,
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())

        val completedNotification =
            NotificationCompat.Builder(this, CHECK_FOR_UPDATE_APK_CHANNEL)
                .setContentTitle(getString(R.string.download_complete))
                .setContentText(getString(R.string.tap_to_open))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(notificationId, completedNotification)
    }
}