package com.paulcoding.hviewer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.helper.GlobalData
import com.paulcoding.hviewer.helper.host
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.helper.setupTextmate
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.ui.component.NewAppReleaseDialog
import com.paulcoding.hviewer.ui.component.ToastExit
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.downloads.downloadsNavigation
import com.paulcoding.hviewer.ui.page.editor.editorNavigation
import com.paulcoding.hviewer.ui.page.editor.listScriptNavigation
import com.paulcoding.hviewer.ui.page.favorite.favoriteNavigation
import com.paulcoding.hviewer.ui.page.history.historyNavigation
import com.paulcoding.hviewer.ui.page.lock.LockPage
import com.paulcoding.hviewer.ui.page.settings.SettingsViewModel
import com.paulcoding.hviewer.ui.page.settings.settingsNavigation
import com.paulcoding.hviewer.ui.page.sites.navToPost
import com.paulcoding.hviewer.ui.page.sites.sitesNavigation
import com.paulcoding.hviewer.ui.page.tabs.TabsViewModel
import com.paulcoding.hviewer.ui.page.tabs.tabsNavigation
import com.paulcoding.hviewer.ui.theme.HViewerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTextmate()

        enableEdgeToEdge()

        setContent {
            Content(intent = intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setContent {
            Content(intent = intent)
        }
    }
}

@Composable
fun Content(intent: Intent?) {
    val viewModel: SettingsViewModel = koinViewModel()
    val tabsViewModel: TabsViewModel = koinViewModel()

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val window = (context as ComponentActivity).window
    val navController = rememberNavController()

    fun handleIntentUrl(url: String) {
        val postItem = PostItem(url = url)
        if (GlobalData.siteConfigMap[url.host] != null) {
            navController.navToPost(postItem)
        } else {
            makeToast(context.getString(R.string.invalid_url, url))
        }
    }

    LaunchedEffect(intent) {
        intent?.run {
            when (action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == type) {
                        getStringExtra(Intent.EXTRA_TEXT)?.let {
                            handleIntentUrl(it)
                        }
                    }
                }

                Intent.ACTION_VIEW -> {
                    handleIntentUrl(data.toString())
                }

                ACTION_INSTALL_APK -> {
                    viewModel.dispatch(SettingsViewModel.Action.InstallApk(data.toString().toUri()))
                }

                else -> {
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.checkedForUpdateAtLaunch) {
            viewModel.dispatch(SettingsViewModel.Action.CheckForAppUpdate())
        }
    }

    LaunchedEffect(uiState.isSecureScreenEnabled) {
        window.setSecureScreen(uiState.isSecureScreenEnabled)
    }

    ToastExit()

    HViewerTheme {
        uiState.newRelease?.let {
            NewAppReleaseDialog(
                release = it,
                onAction = viewModel::dispatch
            )
        }

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

        if (uiState.isLockScreenEnabled)
            LockPage(
                onUnlocked = { viewModel.dispatch(SettingsViewModel.Action.Unlock) }
            )
    }
}



