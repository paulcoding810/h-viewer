package com.paulcoding.hviewer.ui.page.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.ui.component.HLoading


@Composable
fun ImportDialog(
    importState: FavoriteViewModel.ImportState,
    onDismiss: () -> Unit,
    onAction: (FavoriteViewModel.Action) -> Unit
) {
    var importData by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = true
        )
    ) {
        val focusManager = LocalFocusManager.current

        fun onSubmit() {
            val trimmedData = importData.trim()
            importData = trimmedData
            onAction(FavoriteViewModel.Action.Import(trimmedData))
            focusManager.clearFocus()
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.import_posts), style = MaterialTheme.typography.titleSmall)

                OutlinedTextField(
                    value = importData,
                    placeholder = { Text(stringResource(R.string.input_json_placeholder)) },
                    onValueChange = { importData = it },
                    modifier = Modifier.height(150.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { onSubmit() }),
                    maxLines = 5,
                    isError = importState.error != null,
                    supportingText = {
                        importState.error?.let {
                            Text(text = importState.error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                if (importState.isLoading) {
                    HLoading()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.End
                    )
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    FilledTonalButton(
                        onClick = ::onSubmit,
                        enabled = !importState.isLoading
                    ) {
                        Text(stringResource(R.string.submit))
                    }
                }
            }
        }
    }
}

class ImportDialogPreviewParamProvider : PreviewParameterProvider<FavoriteViewModel.ImportState> {
    override val values: Sequence<FavoriteViewModel.ImportState>
        get() = sequenceOf(
            FavoriteViewModel.ImportState(isLoading = true),
            FavoriteViewModel.ImportState(isSuccess = true, successCount = 5),
            FavoriteViewModel.ImportState(error = "Error"),
            FavoriteViewModel.ImportState(isLoading = false)
        )
}

@Preview
@Composable
private fun PreviewImportDialog(
    @PreviewParameter(ImportDialogPreviewParamProvider::class) param: FavoriteViewModel.ImportState,
) {
    ImportDialog(
        importState = param,
        onDismiss = {},
        onAction = {}
    )
}