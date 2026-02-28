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
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.repository.SiteConfigsRepository
import java.time.LocalDateTime
import kotlin.random.Random

class UpdateScriptsWorker(
    val context: Context,
    private val siteConfigsRepository: SiteConfigsRepository,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val localSiteConfigs = siteConfigsRepository.getLocalConfigs().getOrNull() ?: return Result.success()
            val remoteSiteConfigs = siteConfigsRepository.getRemoteConfigs().getOrThrow()
            notify(context, localSiteConfigs, remoteSiteConfigs)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            val data = Data.Builder().putString("error", e.message).build()
            return Result.failure(data)
        }
    }

    private suspend fun notify(context: Context, localSiteConfigs: SiteConfigs, remoteSiteConfigs: SiteConfigs) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notificationBuilder = NotificationCompat.Builder(context, CHECK_FOR_UPDATE_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when {
            remoteSiteConfigs.version == localSiteConfigs.version -> {
                if (BuildConfig.DEBUG) {
                    notificationBuilder
                        .setContentTitle(context.getString(R.string.scripts_up_to_date))
                        .setContentText(LocalDateTime.now().toString())
                    notificationManager.notify(Random.nextInt(2, 100), notificationBuilder.build())
                }
            }

            remoteSiteConfigs.version > localSiteConfigs.version -> {
                siteConfigsRepository.saveRemoteScripts(remoteSiteConfigs)
                    .onSuccess {
                        notificationBuilder
                            .setContentTitle(context.getString(R.string.new_scripts_available))
                            .setContentText(
                                context.getString(
                                    R.string.version_,
                                    remoteSiteConfigs.toString()
                                )
                            )
                        notificationManager.notify(1, notificationBuilder.build())
                    }
                    .onFailure {
                        it.printStackTrace()
                    }
            }
        }
    }
}
