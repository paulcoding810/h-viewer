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
    goBack: () -> Unit,
    navToCustomTag: (Tag) -> Unit,
    navToImages: (PostItem) -> Unit
) {
    val appState by appViewModel.stateFlow.collectAsState()
    val tag = appViewModel.getCurrentTag()
    var pageProgress by remember { mutableStateOf(1 to 1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tag.name.toCapital()) },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
                actions = {
                    HPageProgress(pageProgress.first, pageProgress.second)
                }
            )
        }
    ) { paddings ->
        Box(modifier = Modifier.padding(paddings)) {

            PageContent(
                appViewModel,
                siteConfig = appState.site.second,
                tag = tag,
                navToCustomTag = {
                    if (it.name != tag.name)
                        navToCustomTag(it)
                },
                onPageChange = { currentPage, total ->
                    pageProgress = currentPage to total
                }) { post ->
                navToImages(post)
            }
        }
    }
}

