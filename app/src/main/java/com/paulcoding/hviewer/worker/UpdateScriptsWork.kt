package com.paulcoding.hviewer.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.paulcoding.hviewer.CHECK_FOR_UPDATE_CHANNEL
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.network.SiteConfigsState

class UpdateScriptsWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val result = Github.checkVersionOrUpdate()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            val notificationBuilder = NotificationCompat.Builder(context, CHECK_FOR_UPDATE_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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

                else -> {}
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            val data = Data.Builder().putString("error", e.message).build()
            return Result.failure(data)
        }
    }
}