package com.paulcoding.hviewer.worker

import android.content.Context

class ScheduleWorkerImpl() : ScheduleWorker {
    override fun schedule(context: Context) {
        scheduleScriptsUpdate(context)
    }
}