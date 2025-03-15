package com.paulcoding.hviewer.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R


@Composable
fun ConfirmDialog(
    showDialog: Boolean,
    title: String = "",
    text: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = title) },
            text = { Text(text = text) },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(
                        stringResource(R.string.confirm),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
}
