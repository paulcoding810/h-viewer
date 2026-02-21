@file:Suppress("unused")

package com.paulcoding.hviewer.ui.page.editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import io.github.rosemoe.sora.text.Content
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorPage(
    viewModel: EditorViewModel = koinViewModel(),
    goBack: () -> Boolean,
) {
    val uiState by viewModel.uiState.collectAsState()

    val state = rememberCodeEditorState(
        initialContent = Content(uiState.content), editable = uiState.editable,
        language = uiState.language
    )
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.editor)) }, navigationIcon = {
                HBackIcon {
                    goBack()
                }
            }, actions = {
                HIcon(imageVector = Icons.Outlined.Save) {
                    viewModel.saveScript(state.content.toString())
                    localSoftwareKeyboardController?.hide()
                    goBack()
                    makeToast(R.string.saved)
                }
            })
        },
    ) { paddings ->
        CodeEditor(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize(),
            state = state
        )
    }
}