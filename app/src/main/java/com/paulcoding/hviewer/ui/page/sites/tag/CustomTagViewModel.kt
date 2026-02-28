package com.paulcoding.hviewer.ui.page.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.page.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomTagViewModel(savedStateHandle: SavedStateHandle, val tabsManager: TabsManager) : ViewModel() {
    private var arguments = savedStateHandle.toRoute<Routes.CustomTag>()

    private val _uiState = MutableStateFlow(
        UiState(
            tag = Tag(
                name = arguments.name,
                url = arguments.url
            )
        )
    )
    val uiState = _uiState.asStateFlow()
}

data class UiState(
    val tag: Tag
)