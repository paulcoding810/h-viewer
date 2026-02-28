package com.paulcoding.hviewer.ui.page.editor

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Javascript
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.paulcoding.hviewer.helper.crashLogDir
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.model.ListScriptType
import com.paulcoding.hviewer.ui.page.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ListScriptViewModel(private val context: Context, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val type = savedStateHandle.toRoute<Routes.ListScript>().type

    private val _uiState = MutableStateFlow(
        when (type) {
            ListScriptType.Script -> UiState(
                type = type,
                files = listScriptFiles,
                title = "List Script",
                icon = Icons.Outlined.Javascript
            )

            ListScriptType.Crash -> UiState(
                type = type,
                files = listCrashLogFiles,
                title = "List Crash Log",
                icon = Icons.Outlined.BugReport
            )

        }
    )
    val uiState = _uiState.asStateFlow()

    private val listScriptFiles: List<File>
        get() = context.scriptsDir.listFiles()
            ?.toList()
            ?.filter { it.extension == "json" || it.extension == "js" }
            ?.sortedBy { it.name }
            ?: listOf()

    private val listCrashLogFiles: List<File>
        get() = context.crashLogDir.listFiles()?.filter { it.isFile }
            ?.sortedByDescending { it.lastModified() } ?: listOf()
}

data class UiState(
    val type : ListScriptType,
    val files: List<File>,
    val title: String,
    val icon: ImageVector,
)