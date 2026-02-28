package com.paulcoding.hviewer.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.ui.page.settings.SettingsViewModel

@Composable
fun NewAppReleaseDialog(release: HRelease, onAction: (SettingsViewModel.Action) -> Unit) {
    ConfirmDialog(
        title = stringResource(R.string.update_available),
        text = "${stringResource(R.string.version_, release.version)}\n${release.body}",
        confirmColor = MaterialTheme.colorScheme.primary,
        confirmText = stringResource(R.string.install_now),
        dismissColor = MaterialTheme.colorScheme.onBackground,
        onDismiss = {
            onAction(SettingsViewModel.Action.DismissAppUpdate)
        },
        onConfirm = {
            onAction(SettingsViewModel.Action.DownloadAndInstallApk)
        }
    )
}