package com.paulcoding.hviewer.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleScriptsUpdate(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val updateScriptsWorkRequest =
        PeriodicWorkRequestBuilder<UpdateScriptsWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(calculateDelayUntilMidnight(), TimeUnit.SECONDS)
            .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "updateScripts",
        ExistingPeriodicWorkPolicy.KEEP,
        updateScriptsWorkRequest
    )
}

fun calculateDelayUntilMidnight(): Long {
    val now = Calendar.getInstance()
    val midnight = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
    }
    return midnight.timeInMillis - now.timeInMillis
}