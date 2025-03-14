package com.paulcoding.hviewer.ui.page.posts

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
import com.paulcoding.hviewer.ui.page.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTagPage(
    appViewModel: AppViewModel,
    tag: Tag,
    goBack: () -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    navToTabs: () -> Unit,
    navToImages: (PostItem) -> Unit
) {
    var pageProgress by remember { mutableStateOf(1 to 1) }
    var tabsIconPosition by remember { mutableStateOf(Offset.Zero) }
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tag.name.toCapital()) },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
                actions = {
                    HPageProgress(pageProgress.first, pageProgress.second)
                    TabsIcon(
                        onClick = navToTabs,
                        size = tabs.size,
                        onGloballyPositioned = { tabsIconPosition = it })
                }
            )
        }
    ) { paddings ->
        Box(modifier = Modifier.padding(paddings)) {
            PageContent(
                appViewModel,
                tag = tag,
                endPos = tabsIconPosition,
                onPageChange = { currentPage, total ->
                    pageProgress = currentPage to total
                },
                navToCustomTag = { post, newTag ->
                    if (newTag.name != tag.name)
                        navToCustomTag(post, newTag)
                }) { post ->
                navToImages(post)
            }
        }
    }
}

