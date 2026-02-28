package com.paulcoding.hviewer.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R


@Composable
fun ConfirmDialog(
    title: String = "",
    text: String = "",
    confirmColor: Color? = null,
    dismissColor: Color? = null,
    confirmText: String? = null,
    dismissText: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(
                    confirmText ?: stringResource(R.string.confirm),
                    color = confirmColor ?: MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    dismissText ?: stringResource(R.string.cancel),
                    color = dismissColor ?: MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}
