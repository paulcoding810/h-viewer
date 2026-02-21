package com.paulcoding.hviewer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.helper.setupTextmate
import com.paulcoding.hviewer.ui.component.HLoading
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
        viewModel.dispatch(SettingsViewModel.Action.CheckForAppUpdate())
    }

    LaunchedEffect(uiState.isSecureScreenEnabled) {
        window.setSecureScreen(uiState.isSecureScreenEnabled)
    }

    ToastExit()

    HViewerTheme {
        if (uiState.newRelease != null) UpdateDialog()
        //if (isLocked) {
        //    LockPage(
        //        onUnlocked = appViewModel::unlock
        //    )
        //} else {
        //    AppEntry(appViewModel = appViewModel, intent = intent)
        //}

        AppEntry(intent = intent)

        if (uiState.isLockScreenEnabled)
            LockPage(
                onUnlocked = { viewModel.dispatch(SettingsViewModel.Action.Unlock) }
            )
    }
}

@Composable
fun UpdateDialog() {
    Dialog(onDismissRequest = {}) {
        Surface {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(stringResource(R.string.checking_for_update))
                HLoading()
            }
        }
    }
}



