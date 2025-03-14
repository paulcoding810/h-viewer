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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.crashLogDir
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.helper.readFile
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.helper.writeFile
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import io.github.rosemoe.sora.text.Content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorPage(
    appViewModel: AppViewModel,
    type: String,
    goBack: () -> Boolean,
    scriptFile: String
) {
    val context = LocalContext.current
    val dir = when (type) {
        "script" -> context.scriptsDir
        "crash_log" -> context.crashLogDir
        else -> return makeToast("Unknown type $type")
    }
    val editable = type == "script"
    val language = if (type == "script") "js" else null
    val script = context.readFile(scriptFile, dir)
    val state = rememberCodeEditorState(
        initialContent = Content(script), editable = editable,
        language = language
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
                    context.writeFile(state.content.toString(), scriptFile)
                    appViewModel.refreshLocalConfigs()
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