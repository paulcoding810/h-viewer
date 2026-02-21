package com.paulcoding.hviewer.ui.page

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.paulcoding.hviewer.ui.page.downloads.downloadsNavigation
import com.paulcoding.hviewer.ui.page.editor.editorNavigation
import com.paulcoding.hviewer.ui.page.editor.listScriptNavigation
import com.paulcoding.hviewer.ui.page.favorite.favoriteNavigation
import com.paulcoding.hviewer.ui.page.history.historyNavigation
import com.paulcoding.hviewer.ui.page.settings.settingsNavigation
import com.paulcoding.hviewer.ui.page.sites.sitesNavigation
import com.paulcoding.hviewer.ui.page.tabs.TabsViewModel
import com.paulcoding.hviewer.ui.page.tabs.tabsNavigation
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppEntry(intent: Intent?) {
    val navController = rememberNavController()

    // handle intent
    val updatedIntent by rememberUpdatedState(intent)

    //fun handleIntentUrl(url: String) {
    //    val postItem = PostItem(url = url)
    //    if (postItem.getSiteConfig(hostsMap) != null) {
    //        //navToImages(postItem)
    //    } else {
    //        makeToast(context.getString(R.string.invalid_url, url))
    //    }
    //}

    LaunchedEffect(updatedIntent) {
        updatedIntent?.apply {
            when (action) {
                //Intent.ACTION_SEND -> {
                //    if ("text/plain" == type) {
                //        getStringExtra(Intent.EXTRA_TEXT)?.let {
                //            handleIntentUrl(it)
                //        }
                //    }
                //}
                //
                //Intent.ACTION_VIEW -> {
                //    // TODO: why deeplink not working
                //    if (data.toString().startsWith("hviewer://")) {
                //        val route = data.toString().substringAfter("hviewer://")
                //        navController.navigate(route)
                //    } else {
                //        handleIntentUrl(data.toString())
                //    }
                //}
                //
                //ACTION_INSTALL_APK -> {
                //    appViewModel.installApk(context, data.toString().toUri())
                //}

                else -> {
                }
            }
        }
    }
    val tabsViewModel: TabsViewModel = koinViewModel()

    NavHost(navController, startDestination = Routes.Sites) {
        sitesNavigation(navController)
        tabsNavigation(navController, tabsViewModel)
        favoriteNavigation(navController)
        historyNavigation(navController)
        settingsNavigation(navController)
        listScriptNavigation(navController)
        editorNavigation(navController)
        downloadsNavigation(navController)
    }
}
