package com.paulcoding.hviewer.ui.page.editor

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.paulcoding.hviewer.helper.crashLogDir
import com.paulcoding.hviewer.helper.readFile
import com.paulcoding.hviewer.helper.scriptsDir
import com.paulcoding.hviewer.helper.writeFile
import com.paulcoding.hviewer.model.ListScriptType
import com.paulcoding.hviewer.repository.SiteConfigsRepository
import com.paulcoding.hviewer.ui.page.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditorViewModel(
    private val context: Context,
    private val configsRepository: SiteConfigsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val arguments = savedStateHandle.toRoute<Routes.Editor>()
    private val type = arguments.type
    private val fileName = arguments.fileName

    private val _uiState = MutableStateFlow(
        when (type) {
            ListScriptType.Script -> UiState(
                language = "js",
                editable = true,
                content = context.readFile(fileName, context.scriptsDir)
            )

            ListScriptType.Crash -> UiState(
                language = null,
                editable = false,
                content = context.readFile(fileName, context.crashLogDir),
            )
        }
    )
    val uiState = _uiState.asStateFlow()

    fun saveScript(content: String) {
        context.writeFile(content, fileName)
        configsRepository.refreshConfigs()
    }


    data class UiState(
        val language: String?,
        val editable: Boolean,
        val content: String
    )
}

