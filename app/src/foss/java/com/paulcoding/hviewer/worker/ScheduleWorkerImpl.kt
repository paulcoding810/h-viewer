package com.paulcoding.hviewer.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.paulcoding.hviewer.helper.calculateDelayUntilMidnight
import java.util.concurrent.TimeUnit

class ScheduleWorkerImpl() : ScheduleWorker {
    override fun schedule(context: Context) {
        scheduleScriptsUpdate(context)
        scheduleApkUpdate(context)
    }

    private fun scheduleApkUpdate(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val updateScriptsWorkRequest =
            PeriodicWorkRequestBuilder<UpdateApkWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(calculateDelayUntilMidnight(), TimeUnit.MILLISECONDS)
                .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "updateApk",
            ExistingPeriodicWorkPolicy.KEEP,
            updateScriptsWorkRequest
        )
    }
}