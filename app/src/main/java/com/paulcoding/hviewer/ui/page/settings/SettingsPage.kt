package com.paulcoding.hviewer.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paulcoding.hviewer.MainActivity
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.component.H7Tap
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.icon.EditIcon
import com.paulcoding.hviewer.ui.page.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(appViewModel: AppViewModel, goBack: () -> Boolean, onLockEnabled: () -> Unit) {
    val githubState by Github.stateFlow.collectAsState()
    val prevSiteConfigs = remember { githubState.siteConfigs }
    var modalVisible by remember { mutableStateOf(false) }
    var secureScreen by remember { mutableStateOf(Preferences.secureScreen) }
    val window = (LocalContext.current as MainActivity).window
    var lockModalVisible by remember { mutableStateOf(false) }
    var appLockEnabled by remember { mutableStateOf(Preferences.pin.isNotEmpty()) }

    LaunchedEffect(githubState.siteConfigs) {
        if (prevSiteConfigs != githubState.siteConfigs) {
            goBack()
        }
    }

    fun onAppLockEnabled(pin: String) {
        Preferences.pin = pin
        appLockEnabled = true
        onLockEnabled()
    }

    fun onAppLockDisabled() {
        Preferences.pin = ""
        appLockEnabled = false
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Settings") }, navigationIcon = {
            HBackIcon { goBack() }
        })
    }) { paddings ->
        Box(modifier = Modifier.padding(paddings)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Remote Url", modifier = Modifier.weight(1f))
                    IconButton(onClick = { modalVisible = true }) {
                        Icon(EditIcon, "Edit")
                    }
                }
                Text(
                    githubState.remoteUrl.ifEmpty { "https://github.com/{OWNER}/{REPO}/" },
                    textDecoration = TextDecoration.Underline
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Enable secure screen", modifier = Modifier.weight(1f))
                    Checkbox(checked = secureScreen, onCheckedChange = {
                        secureScreen = it
                        Preferences.secureScreen = it
                        window.setSecureScreen(it)
                    })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Lock Screen", modifier = Modifier.weight(1f))
                    Switch(checked = appLockEnabled, onCheckedChange = { locked ->
                        if (locked) {
                            lockModalVisible = true
                        } else {
                            onAppLockDisabled()
                        }
                    })
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    H7Tap(modifier = Modifier.align(Alignment.BottomCenter)) {
                        appViewModel.setDevMode(it)
                    }
                }
            }
        }

        if (modalVisible)
            InputRemoteModal(
                initialText = githubState.remoteUrl,
                setVisible = {
                    modalVisible = it
                }) {
                Github.updateRemoteUrl(it)
            }

        if (lockModalVisible)
            LockModal(onDismiss = { lockModalVisible = false }) {
                onAppLockEnabled(it)
            }
    }
}