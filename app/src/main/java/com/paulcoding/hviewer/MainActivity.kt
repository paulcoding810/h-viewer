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
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.helper.setupTextmate
import com.paulcoding.hviewer.ui.component.NewAppReleaseDialog
import com.paulcoding.hviewer.ui.component.ToastExit
import com.paulcoding.hviewer.ui.page.AppEntry
import com.paulcoding.hviewer.ui.page.lock.LockPage
import com.paulcoding.hviewer.ui.page.settings.SettingsViewModel
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
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val window = (context as ComponentActivity).window

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

        AppEntry(intent = intent)

        if (uiState.isLockScreenEnabled)
            LockPage(
                onUnlocked = { viewModel.dispatch(SettingsViewModel.Action.Unlock) }
            )
    }
}



