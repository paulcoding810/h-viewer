package com.paulcoding.hviewer.ui.page.settings

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.extensions.setSecureScreen
import com.paulcoding.hviewer.model.ListScriptType
import com.paulcoding.hviewer.ui.component.H7Tap
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.NewAppReleaseDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    viewModel: SettingsViewModel = koinViewModel(),
    navToListScript: (ListScriptType) -> Unit,
    goBack: () -> Boolean,
) {
    val context = LocalContext.current
    val window = (context as ComponentActivity).window

    val scrollState = rememberScrollState()

    val uiState by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState()


    fun onAppLockEnabled(pin: String) {
        viewModel.dispatch(SettingsViewModel.Action.SetPin(pin))
    }

    fun onAppLockDisabled() {
        viewModel.dispatch(SettingsViewModel.Action.DisableLock)
    }

    LaunchedEffect(effect) {
        when (val _effect = effect) {
            is SettingsViewModel.Effect.Toast -> {
                Toast.makeText(context, _effect.message, Toast.LENGTH_LONG).show()
            }
            null -> {}
        }
        viewModel.dispatch(SettingsViewModel.Action.ConsumeEffect(effect))
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
                SettingRow(
                    stringResource(R.string.remote_url),
                    description = uiState.remoteUrl,
                    onClick = {
                        viewModel.dispatch(SettingsViewModel.Action.ToggleRemoteModal(true))
                    }) {
                    Icon(Icons.Outlined.Edit, "Edit remote url")
                }

                HorizontalDivider()

                SettingRow(stringResource(R.string.enable_secure_screen)) {
                    SettingSwitch(
                        checked = uiState.isSecureScreenEnabled,
                        onCheckedChange = {
                            viewModel.dispatch(SettingsViewModel.Action.SetSecureScreen(it))
                            window.setSecureScreen(it)
                        })
                }

                HorizontalDivider()

                SettingRow(stringResource(R.string.lock_screen)) {
                    SettingSwitch(checked = uiState.isLockScreenEnabled, onCheckedChange = { locked ->
                        if (locked) {
                            viewModel.dispatch(SettingsViewModel.Action.SetEditPinModalVisible(true))
                        } else {
                            onAppLockDisabled()
                        }
                    })
                }

                HorizontalDivider()

                if (uiState.isDevMode) {
                    SettingRow(
                        stringResource(R.string.open_crash_log),
                        onClick = { navToListScript(ListScriptType.Crash) }
                    ) {
                        Icon(Icons.Outlined.BugReport, "Open crash log files", Modifier.size(24.dp))
                    }
                    HorizontalDivider()

                    SettingRow(
                        stringResource(R.string.edit_local_scripts),
                        onClick = { navToListScript(ListScriptType.Script) }
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
                    viewModel.dispatch(SettingsViewModel.Action.SetDevMode(true))
                }
                HIcon(Icons.Outlined.Update, tint = MaterialTheme.colorScheme.primary) {
                    viewModel.dispatch(SettingsViewModel.Action.CheckForAppUpdate(showToast = true))
                }
            }
        }
    }

    if (uiState.showRemoteModal) InputRemoteModal(
        initialText = uiState.remoteUrl,
        initialBranch = uiState.remoteBranch,
        onDismiss = {
            viewModel.dispatch(SettingsViewModel.Action.ToggleRemoteModal(false))
        }) { repo, branch ->
        viewModel.dispatch(SettingsViewModel.Action.UpdateRemoteUrl(repo, branch))
    }

    if (uiState.showOinModal) {
        LockModal(
            onDismiss = { viewModel.dispatch(SettingsViewModel.Action.SetEditPinModalVisible(false)) },
            onPinConfirmed = { onAppLockEnabled(it) }
        )
    }

    uiState.newRelease?.let {
        NewAppReleaseDialog(
            release = it,
            onAction = viewModel::dispatch
        )
    }

    if (uiState.isUpdating) {
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