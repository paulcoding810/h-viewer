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
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.helper.readFile
import com.paulcoding.hviewer.helper.writeFile
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import io.github.rosemoe.sora.text.Content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorPage(appViewModel: AppViewModel, goBack: () -> Boolean, scriptFile: String) {
    val context = LocalContext.current
    val script = context.readFile(scriptFile)
    val state = rememberCodeEditorState(initialContent = Content(script))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "Editor") }, navigationIcon = {
                HBackIcon {
                    goBack()
                }
            }, actions = {
                HIcon(imageVector = Icons.Outlined.Save) {
                    context.writeFile(state.content.toString(), scriptFile)
                    Github.refreshLocalConfigs()
                    makeToast("Saved!")
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