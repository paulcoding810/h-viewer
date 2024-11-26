package com.paulcoding.hviewer.ui.page.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HImage
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.model.SiteConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPage(siteConfig: SiteConfig, postUrl: String, goBack: () -> Unit) {
    val viewModel: PostViewModel = viewModel(
        factory = PostViewModelFactory(postUrl, siteConfig = siteConfig)
    )

    val uiState by viewModel.stateFlow.collectAsState()
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        viewModel.getImages()
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextImages()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                HBackIcon { goBack() }
            },
            title = {},
        )
    }) {

        LazyColumn(
            state = listState,
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.images) { image ->
                HImage(image)
            }
            if (uiState.isLoading)
                item {
                    HLoading()
                }
        }
    }
}