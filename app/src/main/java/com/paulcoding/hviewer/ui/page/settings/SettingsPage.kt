package com.paulcoding.hviewer.ui.page.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.component.ConfirmDialog
import com.paulcoding.hviewer.ui.component.H7Tap
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.page.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    appViewModel: AppViewModel,
    goBack: () -> Boolean,
    navToListScript: () -> Unit,
    navToListCrashLog: () -> Unit
) {
    val appState by appViewModel.stateFlow.collectAsState()
    var modalVisible by remember { mutableStateOf(false) }
    var secureScreen by remember { mutableStateOf(Preferences.secureScreen) }
    val context = LocalContext.current
    val window = (context as ComponentActivity).window
    var lockModalVisible by remember { mutableStateOf(false) }
    var appLockEnabled by remember { mutableStateOf(Preferences.pin.isNotEmpty()) }
    var newVersion by remember { mutableStateOf("") }
    var downloadUrl by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    fun onAppLockEnabled(pin: String) {
        Preferences.pin = pin
        appLockEnabled = true
        appViewModel.lock()
    }

    fun onAppLockDisabled() {
        Preferences.pin = ""
        appLockEnabled = false
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
            HBackIcon { goBack() }
        })
    }) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingRow(stringResource(R.string.remote_url),
                    description = Preferences.getRemote()
                        .ifEmpty { "https://github.com/{OWNER}/{REPO}/" },
                    onClick = {
                        modalVisible = true
                    }) {
                    Icon(Icons.Outlined.Edit, "Edit remote url")
                }

                HorizontalDivider()

                SettingRow(stringResource(R.string.enable_secure_screen)) {
                    SettingSwitch(checked = secureScreen,
                        onCheckedChange = {
                            secureScreen = it
                            Preferences.secureScreen = it
                            window.setSecureScreen(it)
                        })
                }

                HorizontalDivider()

                SettingRow(stringResource(R.string.lock_screen)) {
                    SettingSwitch(checked = appLockEnabled, onCheckedChange = { locked ->
                        if (locked) {
                            lockModalVisible = true
                        } else {
                            onAppLockDisabled()
                        }
                    })
                }

                HorizontalDivider()

                if (appState.isDevMode) {
                    SettingRow(
                        stringResource(R.string.open_crash_log),
                        onClick = navToListCrashLog
                    ) {
                        Icon(Icons.Outlined.BugReport, "Open crash log files", Modifier.size(24.dp))
                    }
                    HorizontalDivider()

                    SettingRow(
                        stringResource(R.string.edit_local_scripts),
                        onClick = navToListScript
                    ) {
                        Icon(
                            Icons.Outlined.Description,
                            "Edit local scripts",
                            Modifier.size(24.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                H7Tap() {
                    appViewModel.setDevMode(it)
                }
                HIcon(Icons.Outlined.Update, tint = MaterialTheme.colorScheme.primary) {
                    appViewModel.checkForUpdate(BuildConfig.VERSION_NAME,
                        onUpToDate = {
                            makeToast(R.string.up_to_date)
                        },
                        onUpdateAvailable = { version, url ->
                            newVersion = version
                            downloadUrl = url
                        })
                }
            }
        }
    }

    if (modalVisible) InputRemoteModal(
        initialText = Preferences.getRemote(),
        initialBranch = Preferences.branch,
        setVisible = {
            modalVisible = it
        }) { repo, branch ->
        Preferences.branch = branch
        appViewModel.checkVersionOrUpdate(repo, onUpdate = { state ->
            makeToast(state.getToastMessage())
            goBack()
        })
    }

    if (lockModalVisible) LockModal(onDismiss = { lockModalVisible = false }) {
        onAppLockEnabled(it)
    }

    ConfirmDialog(
        showDialog = newVersion.isNotEmpty(),
        title = stringResource(R.string.update_available),
        text = newVersion,
        confirmColor = MaterialTheme.colorScheme.primary,
        confirmText = stringResource(R.string.install_now),
        dismissColor = MaterialTheme.colorScheme.onBackground,
        onDismiss = {
            newVersion = ""
        },
        onConfirm = {
            appViewModel.downloadAndInstallApk(context, downloadUrl)
            newVersion = ""
        }
    )

    if (appState.updatingApk) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                HLoading()
            }
        }
    }
}

@Composable
internal fun SettingRow(
    title: String = "",
    description: String? = null,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            if (!description.isNullOrEmpty()) Text(description, fontSize = 11.sp)
        }
        content()
    }
}

@Composable
internal fun SettingSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 24.dp) {
        Switch(
            modifier = modifier,
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource,
            thumbContent = thumbContent,
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}