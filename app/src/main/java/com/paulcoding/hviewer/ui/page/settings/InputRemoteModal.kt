package com.paulcoding.hviewer.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.network.Github


@Composable
fun InputRemoteModal(
    initialText: String = "",
    initialBranch: String = "main",
    setVisible: (Boolean) -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(initialText)) }
    var branch by remember { mutableStateOf(initialBranch) }
    val focusRequester = remember { FocusRequester() }

    fun submit() {
        val url = Github.parseRemoteUrl(textFieldValue.text)
        if (url.isNullOrEmpty()) {
            return makeToast(R.string.invalid_repo)
        } else {
            setVisible(false)
            onSubmit(textFieldValue.text, branch)
        }
    }

    fun dismiss() {
        setVisible(false)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                textFieldValue = textFieldValue.copy(
                                    selection = TextRange(
                                        0,
                                        textFieldValue.text.length
                                    )
                                )
                            }
                        },
                    label = { Text(stringResource(R.string.remote_url)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = { submit() }
                    ),
                    placeholder = { Text(stringResource(R.string.h_viewer_scripts_url)) }
                )
                OutlinedTextField(value = branch, onValueChange = { branch = it }, label = {
                    Text(
                        stringResource(R.string.branch)
                    )
                })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { dismiss() }) {
                        Text(
                            stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(onClick = { submit() }) {
                        Text(stringResource(R.string.ok), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}