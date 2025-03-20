package com.paulcoding.hviewer.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.CHECK_FOR_UPDATE_APK_CHANNEL
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.network.Github

class UpdateApkWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val notificationId = 2

    override suspend fun doWork(): Result {
        try {
            val hRelease = Github.checkForUpdate(BuildConfig.VERSION_NAME)
            if (hRelease != null) {
                notify(context, hRelease)
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            val data = Data.Builder().putString("error", e.message).build()
            return Result.failure(data)
        }
    }

    private fun notify(context: Context, release: HRelease) {
        val intent = Intent(context, DownloadApkService::class.java).apply {
            putExtra("release", release)
        }

        val pendingIntent =
            PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notificationBuilder = NotificationCompat.Builder(context, CHECK_FOR_UPDATE_APK_CHANNEL)
            .setContentTitle(context.getString(R.string.new_version_available))
            .setContentText(
                context.getString(
                    R.string.version_,
                    release.version,
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .addAction(
                android.R.drawable.ic_media_play,
                context.getString(R.string.install), pendingIntent
            )

            .setAutoCancel(false)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
