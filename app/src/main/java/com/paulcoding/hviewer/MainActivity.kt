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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.helper.setupTextmate
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.ToastExit
import com.paulcoding.hviewer.ui.page.AppEntry
import com.paulcoding.hviewer.ui.theme.HViewerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTextmate()

        window.setSecureScreen(Preferences.secureScreen)

        enableEdgeToEdge()
        setContent {
            Content(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setContent {
            Content(intent)
        }
    }
}

@Composable
fun Content(intent: Intent?) {
    val githubState by Github.stateFlow.collectAsState()
    val repoUrl = githubState.remoteUrl

    ToastExit()

    LaunchedEffect(repoUrl) {
        if (repoUrl.isNotEmpty())
            Github.checkVersionOrUpdate()
    }

    LaunchedEffect(githubState.error) {
        if (githubState.error != null) {
            makeToast(githubState.error?.message ?: "")
        }
    }

    HViewerTheme {
        if (githubState.isLoading)
            UpdateDialog()
        AppEntry(intent)
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



