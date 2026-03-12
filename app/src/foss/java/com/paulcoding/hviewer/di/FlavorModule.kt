package com.paulcoding.hviewer.di

import com.paulcoding.hviewer.repository.UpdateAppRepository
import com.paulcoding.hviewer.repository.UpdateAppRepositoryImpl
import com.paulcoding.hviewer.worker.ScheduleWorker
import com.paulcoding.hviewer.worker.ScheduleWorkerImpl
import com.paulcoding.hviewer.worker.UpdateApkWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val flavorModule = module {
    single<ScheduleWorker> {
        ScheduleWorkerImpl()
    }

    single<UpdateAppRepository> { UpdateAppRepositoryImpl(get(), get()) }

    workerOf(::UpdateApkWorker)
}