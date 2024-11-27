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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.icon.EditIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(goBack: () -> Boolean) {
    val githubState by Github.stateFlow.collectAsState()
    var modalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(githubState.siteConfigs) {
        if (githubState.siteConfigs != null) {
            goBack()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Settings") }, navigationIcon = {
            HBackIcon { goBack() }
        })
    }) { paddings ->
        Box(modifier = Modifier.padding(paddings)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
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
                    githubState.remoteUrl.ifEmpty { "Empty" },
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        if (modalVisible)
            InputRemoteModal(setVisible = {
                modalVisible = it
            }) { Github.updateRemoteUrl(it) }
    }
}

@Composable
fun InputRemoteModal(setVisible: (Boolean) -> Unit, onSubmit: (url: String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    fun submit() {
        setVisible(false)
        onSubmit(text)
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
                    text,
                    onValueChange = { text = it },
                    modifier = Modifier.focusRequester(focusRequester),
                    label = { Text("Remote Url") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { submit() }
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable {
                            dismiss()
                        })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("OK", color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            submit()
                        })
                }
            }
        }
    }
}