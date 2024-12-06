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
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

data class CodeEditorState(
    var editor: CodeEditor? = null,
    val initialContent: Content = Content(),
    val isEditable: Boolean = true,
    val language: String? = null,
) {
    var content by mutableStateOf(initialContent)
    var editable by mutableStateOf(isEditable)
}

private fun setCodeEditorFactory(
    context: Context,
    state: CodeEditorState
): CodeEditor {
    val editor = CodeEditor(context)

    editor.ensureTextmateTheme()
    if (state.language != null) {
        editor.setEditorLanguage(state.language)
    }

    editor.apply {
        setText(state.content)
        setTextSize(14f)
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
    language: String? = null
) = remember {
    CodeEditorState(
        initialContent = initialContent,
        isEditable = editable,
        language = language,
    )
}


private fun CodeEditor.ensureTextmateTheme() {
    var editorColorScheme = colorScheme
    if (editorColorScheme !is TextMateColorScheme) {
        editorColorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
        colorScheme = editorColorScheme
    }
}

private fun CodeEditor.setEditorLanguage(extension: String) {
    val languageScopeName = "source.$extension"
    val language = TextMateLanguage.create(
        languageScopeName, true
    )
    this.setEditorLanguage(language)
}