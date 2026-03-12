package com.paulcoding.hviewer.di

import UpdateAppRepositoryImpl
import com.paulcoding.hviewer.repository.UpdateAppRepository
import com.paulcoding.hviewer.worker.ScheduleWorker
import com.paulcoding.hviewer.worker.ScheduleWorkerImpl
import org.koin.dsl.module

val flavorModule = module {
    single<ScheduleWorker> {
        ScheduleWorkerImpl()
    }

    single<UpdateAppRepository> { UpdateAppRepositoryImpl() }
}