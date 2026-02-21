package com.paulcoding.hviewer.di

import androidx.room.Room
import com.paulcoding.hviewer.database.AppDatabase
import com.paulcoding.hviewer.database.MIGRATION_1_2
import com.paulcoding.hviewer.database.MIGRATION_2_3
import com.paulcoding.hviewer.database.MIGRATION_3_4
import com.paulcoding.hviewer.database.MIGRATION_4_5
import com.paulcoding.hviewer.database.MIGRATION_5_6
import com.paulcoding.hviewer.helper.Downloader
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.network.GithubRemoteDatasource
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.repository.FavoriteRepository
import com.paulcoding.hviewer.repository.HistoryRepository
import com.paulcoding.hviewer.repository.SiteConfigsRepository
import com.paulcoding.hviewer.repository.TabsRepository
import com.paulcoding.hviewer.repository.UpdateAppRepository
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.favorite.FavoriteViewModel
import com.paulcoding.hviewer.ui.page.history.HistoryViewModel
import com.paulcoding.hviewer.ui.page.lock.LockViewModel
import com.paulcoding.hviewer.ui.page.posts.CustomTagViewModel
import com.paulcoding.hviewer.ui.page.settings.SettingsViewModel
import com.paulcoding.hviewer.ui.page.sites.SitesViewModel
import com.paulcoding.hviewer.ui.page.sites.post.PostViewModel
import com.paulcoding.hviewer.ui.page.sites.site.PostsViewModel
import com.paulcoding.hviewer.ui.page.sites.site.SiteViewModel
import com.paulcoding.hviewer.ui.page.tabs.TabViewModel
import com.paulcoding.hviewer.ui.page.tabs.TabsViewModel
import com.paulcoding.hviewer.worker.UpdateApkWorker
import com.paulcoding.hviewer.worker.UpdateScriptsWorker
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::SitesViewModel)
    viewModelOf(::SiteViewModel)
    viewModelOf(::PostViewModel)
    viewModelOf(::TabsViewModel)
    viewModelOf(::TabViewModel)
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::CustomTagViewModel)
    viewModelOf(::LockViewModel)

    // use params as TabsPage contains lots of Pages using TabViewModel
    viewModel { params ->
        TabViewModel(
            postItem = params.get(),
            favoriteRepository = get()
        )
    }
    viewModel { params ->
        PostsViewModel(
            postUrl = params.get(),
            isSearch = params.get(),
            favoriteRepository = get(),
            tabsManager = get(),
            postItemDao = get(),
        )
    }
}

val repositoryModule = module {
    singleOf(::SiteConfigsRepository)
    singleOf(::UpdateAppRepository)
    singleOf(::FavoriteRepository)
    singleOf(::HistoryRepository)
    singleOf(::TabsRepository)
}

val appModule = module {
    singleOf(::Downloader)
    singleOf(::GithubRemoteDatasource)
    singleOf(::Preferences)
    singleOf(::TabsManager)

    single {
        MMKV.defaultMMKV()
    }

    single {
        //DatabaseProvider.getInstance()
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java, "hviewer_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .build()
    }
    single {
        get<AppDatabase>().postItemDao()
    }

    single {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    workerOf(::UpdateApkWorker)
    workerOf(::UpdateScriptsWorker)
}
