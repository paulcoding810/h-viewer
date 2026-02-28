package com.paulcoding.hviewer.ui.page.sites.tag

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.paulcoding.hviewer.extensions.toCapital
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.page.posts.CustomTagViewModel
import com.paulcoding.hviewer.ui.page.sites.composable.TabsIcon
import com.paulcoding.hviewer.ui.page.sites.site.PageContent
import com.paulcoding.hviewer.ui.page.sites.site.PostsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTagPage(
    viewModel: CustomTagViewModel = koinViewModel(),
    goBack: () -> Unit,
    navToCustomTag: (Tag) -> Unit,
    navToTabs: () -> Unit,
    navToImages: (PostItem) -> Unit
) {
    var pageProgress by remember { mutableStateOf(1 to 1) }
    var tabsIconPosition by remember { mutableStateOf(Offset.Zero) }

    val tabsCount by viewModel.tabsManager.tabsSizeFlow.collectAsState(initial = 0)
    val uiState by viewModel.uiState.collectAsState()
    val (name, url) = uiState.tag

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name.toCapital()) },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
                actions = {
                    HPageProgress(pageProgress.first, pageProgress.second)
                    TabsIcon(
                        onClick = navToTabs,
                        size = tabsCount,
                        onGloballyPositioned = { tabsIconPosition = it })
                }
            )
        }
    ) { paddings ->
        Box(modifier = Modifier.padding(paddings)) {
            PageContent(
                viewModel = koinViewModel<PostsViewModel>(
                    key = url,
                    parameters = { parametersOf(url, false) }
                ),
                endPos = tabsIconPosition,
                onPageChange = { currentPage, total ->
                    pageProgress = currentPage to total
                },
                onAddToTabs = viewModel.tabsManager::addTab,
                navToCustomTag = { newTag ->
                    if (newTag.name != uiState.tag.name)
                        navToCustomTag(newTag)
                },
                onClick = (navToImages)
            )
        }
    }
}

