package com.paulcoding.hviewer.ui.page.editor

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

data class CodeEditorState(
    var editor: CodeEditor? = null,
    val initialContent: Content = Content(),
    val isEditable: Boolean = true,
) {
    var content by mutableStateOf(initialContent)
    var editable by mutableStateOf(isEditable)
}

private fun setCodeEditorFactory(
    context: Context,
    state: CodeEditorState
): CodeEditor {
    val editor = CodeEditor(context)
    editor.apply {
        setText(state.content)
        editor.editable = state.editable
    }
    state.editor = editor
    return editor
}

@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    state: CodeEditorState
) {
    val context = LocalContext.current
    val editor = remember {
        setCodeEditorFactory(
            context = context,
            state = state
        )
    }
    AndroidView(
        factory = { editor },
        modifier = modifier,
        onRelease = {
            it.release()
        }
    )
}

@Composable
fun rememberCodeEditorState(
    initialContent: Content = Content(),
    editable: Boolean = true,
) = remember {
    CodeEditorState(
        initialContent = initialContent,
        isEditable = editable,
    )
}