package com.paulcoding.hviewer.ui.page.posts

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.extensions.toCapital
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HImage
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.icon.Search
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsPage(
    navToImages: (postUrl: String) -> Unit,
    navToSearch: () -> Unit,
    siteConfig: SiteConfig,
    goBack: () -> Unit
) {
    val listTopic = siteConfig.tags.keys.toList()
    val pagerState = rememberPagerState { listTopic.size }
    val selectedTabIndex = pagerState.currentPage
    val currentPage = listTopic[selectedTabIndex]
    val scope = rememberCoroutineScope()
    var pageProgress by remember { mutableStateOf(1 to 1) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(currentPage.toCapital()) }, navigationIcon = {
            HBackIcon { goBack() }
        }, actions = {
            HPageProgress(pageProgress.first, pageProgress.second)
            HIcon(imageVector = Search) { navToSearch() }
        })
    }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
            ) {
                listTopic.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = tab) },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { pageIndex ->
                val page = listTopic[pageIndex]
                PageContent(
                    siteConfig,
                    page,
                    onPageChange = { currentPage, total ->
                        pageProgress = currentPage to total
                    }) { postUrl ->
                    navToImages(postUrl)
                }
            }
        }
    }
}

@Composable
fun PageContent(
    siteConfig: SiteConfig,
    topic: String,
    onPageChange: (Int, Int) -> Unit,
    onClick: (String) -> Unit
) {
    val viewModel: PostsViewModel = viewModel(
        factory = PostsViewModelFactory(siteConfig, topic),
        key = topic
    )
    val listState = rememberLazyListState()
    val uiState by viewModel.stateFlow.collectAsState()

    uiState.error?.let {
        Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(uiState.postItems) {
        if (uiState.postItems.isEmpty()) {
            viewModel.getPosts(1)
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostsData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextPosts()
        }
    }

    LaunchedEffect(uiState.postsPage, uiState.postsTotalPage) {
        onPageChange(uiState.postsPage, uiState.postsTotalPage)
    }

    LazyColumn(
        state = listState
    ) {
        items(uiState.postItems) { post ->
            PostItemView(post) { postUrl ->
                onClick(postUrl)
            }
        }
        if (uiState.isLoading)
            item {
                HLoading()
            }
        else if (uiState.postItems.isEmpty())
            item {
                HEmpty(
                    title = "No posts found",
                    message = "Refresh?"
                ) {
                    viewModel.getPosts(1)
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

