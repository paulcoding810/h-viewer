package com.paulcoding.hviewer.ui.page.posts

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HImage
import com.paulcoding.hviewer.ui.component.HLoading


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsPage(
    navToImages: (postUrl: String) -> Unit,
    siteConfig: SiteConfig,
    topic: String,
    goBack: () -> Unit
) {
    val viewModel: PostsViewModel = viewModel(
        factory = PostsViewModelFactory(siteConfig, topic)
    )
    val listState = rememberLazyListState()
    val uiState by viewModel.stateFlow.collectAsState()

    var shouldFetchPosts by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(shouldFetchPosts) {
        if (shouldFetchPosts) {
            viewModel.getPosts(1)
            shouldFetchPosts = false
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostsData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextPosts()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(topic) }, navigationIcon = {
            HBackIcon { goBack() }
        })
    }) { paddings ->

        LazyColumn(
            modifier = Modifier
                .padding(paddings),
            state = listState
        ) {
            items(uiState.postItems) { post ->
                PostItemView(post) { postUrl ->
                    navToImages(postUrl)
                }
            }
            if (uiState.isLoading)
                item {
                    HLoading()
                }
        }
    }
}

@Composable
fun PostItemView(postItem: PostItem, viewPost: (postUrl: String) -> Unit) {
    return Column(modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .clickable {
            viewPost(postItem.url)
        }) {
        Text(postItem.name)
        HImage(
            url = postItem.thumbnail
        )
    }
}

