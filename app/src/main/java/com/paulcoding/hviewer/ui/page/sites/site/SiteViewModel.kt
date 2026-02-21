package com.paulcoding.hviewer.ui.page.sites.site

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.paulcoding.hviewer.helper.GlobalData
import com.paulcoding.hviewer.helper.TabsManager
import com.paulcoding.hviewer.helper.host
import com.paulcoding.hviewer.ui.page.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SiteViewModel(
    savedStateHandle: SavedStateHandle,
    val tabsManager: TabsManager
) : ViewModel() {
    private val arguments = savedStateHandle.toRoute<Routes.Site>()
    private val postUrl = arguments.url

    private val siteConfig = GlobalData.siteConfigMap[postUrl.host]!!

    private var _stateFlow = MutableStateFlow(UiState(tags = siteConfig.tags))
    val stateFlow = _stateFlow.asStateFlow()

    data class UiState(
        val tags: Map<String, String> = mapOf(),
    )
}