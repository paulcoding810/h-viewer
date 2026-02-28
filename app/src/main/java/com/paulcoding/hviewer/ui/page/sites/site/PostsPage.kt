package com.paulcoding.hviewer.ui.page.sites.site

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.toCapital
import com.paulcoding.hviewer.helper.BasePaginationHelper
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.page.sites.composable.PostList
import com.paulcoding.hviewer.ui.page.sites.composable.TabsIcon
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsPage(
    viewModel: SiteViewModel = koinViewModel(),
    viewModelStoreOwner: NavBackStackEntry,
    navToPost: (PostItem) -> Unit,
    navToSearch: () -> Unit,
    navToCustomTag: (Tag) -> Unit,
    navToTabs: () -> Unit,
    goBack: () -> Unit,
) {
    val uiState by viewModel.stateFlow.collectAsState()
    val tabsCount by viewModel.tabsManager.tabsSizeFlow.collectAsState(initial = 0)
    val listTag = uiState.tags.map { (name, url) -> Tag(name = name, url = url) }

    val pagerState = rememberPagerState { listTag.size }
    val selectedTabIndex = pagerState.currentPage
    val currentTag = listTag[selectedTabIndex]
    val scope = rememberCoroutineScope()
    var pageProgress by remember { mutableStateOf(1 to 1) }
    var tabsIconPosition by remember { mutableStateOf(Offset.Zero) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(currentTag.name.toCapital()) }, navigationIcon = {
            HBackIcon { goBack() }
        }, actions = {
            HPageProgress(pageProgress.first, pageProgress.second)
            HIcon(imageVector = Icons.Outlined.Search, onClick = navToSearch)
            TabsIcon(
                size = tabsCount,
                onGloballyPositioned = { tabsIconPosition = it },
                onClick = navToTabs
            )
        })
    }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
            ) {
                listTag.forEachIndexed { index, tag ->
                    Tab(
                        selected = selectedTabIndex == index,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = tag.name) },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { pageIndex ->
                val tag = listTag[pageIndex]
                val viewModel = koinViewModel<PostsViewModel>(
                    viewModelStoreOwner = viewModelStoreOwner,
                    key = tag.url,
                    parameters = { parametersOf(tag.url, false) }
                )
                PageContent(
                    viewModel = viewModel,
                    endPos = tabsIconPosition,
                    navToCustomTag = navToCustomTag,
                    onAddToTabs = viewModel.tabsManager::addTab,
                    onPageChange = { currentPage, total ->
                        pageProgress = currentPage to total
                    }) { post ->
                    navToPost(post)
                }
            }
        }
    }
}

@Composable
internal fun PageContent(
    viewModel: PostsViewModel,
    endPos: Offset,
    onPageChange: (Int, Int) -> Unit,
    onAddToTabs: (PostItem) -> Unit,
    navToCustomTag: (Tag) -> Unit,
    onClick: (PostItem) -> Unit
) {
    val uiState by viewModel.stateFlow.collectAsState()
    val postItems by viewModel.postItems.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(postItems) {
        if (postItems.isEmpty()) {
            viewModel.getPosts(1)
        }
    }

    LaunchedEffect(uiState.postsPage, uiState.postsTotalPage) {
        onPageChange(uiState.postsPage, uiState.postsTotalPage)
    }

    val paginationHelper = remember {
        BasePaginationHelper(
            buffer = 5,
            isLoading = { uiState.isLoading },
            hasMore = viewModel::canLoadMorePostsData,
            loadMore = viewModel::getNextPosts
        )
    }

    PostList(
        paginationHelper = paginationHelper,
        endPos = endPos,
        onAddToTabs = onAddToTabs,
        setFavorite = { post, _ ->
            viewModel.toggleFavorite(post)
        },
        navToCustomTag = navToCustomTag,
        isLoading = uiState.isLoading,
        onRefresh = { viewModel.getPosts(1) },
        onClick = onClick,
        listPosts = postItems,
    )
}