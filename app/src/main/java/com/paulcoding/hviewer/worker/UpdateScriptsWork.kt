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
import com.paulcoding.hviewer.CHECK_FOR_UPDATE_CHANNEL
import com.paulcoding.hviewer.MainActivity
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.network.SiteConfigsState
import java.time.LocalDateTime
import kotlin.random.Random

class UpdateScriptsWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val result = Github.checkVersionOrUpdate()
            notify(context, result)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            val data = Data.Builder().putString("error", e.message).build()
            return Result.failure(data)
        }
    }

    private fun notify(context: Context, result: SiteConfigsState) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notificationBuilder = NotificationCompat.Builder(context, CHECK_FOR_UPDATE_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when (result) {
            is SiteConfigsState.Updated -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.scripts_updated))
                    .setContentText(
                        context.getString(
                            R.string.version_,
                            result.newVersion.toString()
                        )
                    )
                notificationManager.notify(1, notificationBuilder.build())
            }

            is SiteConfigsState.NewConfigsInstall -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.scripts_installed))
                    .setContentText(context.getString(R.string.from_repo_, result.repoUrl))
                notificationManager.notify(1, notificationBuilder.build())
            }

            else -> {
                if (BuildConfig.DEBUG) {
                    notificationBuilder
                        .setContentTitle("Up to date")
                        .setContentText(LocalDateTime.now().toString())
                    notificationManager.notify(Random.nextInt(2, 100), notificationBuilder.build())
                }
            }
        }
    }
}