package com.paulcoding.hviewer.ui.page.posts

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tab
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.extensions.toCapital
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HGoTop
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.tabs.AddToCartAnimation
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsPage(
    appViewModel: AppViewModel,
    navToImages: (PostItem) -> Unit,
    navToSearch: () -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    navToTabs: () -> Unit,
    goBack: () -> Unit
) {
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())

    val siteConfig = appViewModel.getCurrentSiteConfig()

    val listTag: List<Tag> = siteConfig.tags.keys.map { key ->
        Tag(name = key, url = siteConfig.tags[key]!!)
    }
    val pagerState = rememberPagerState { listTag.size }
    val selectedTabIndex = pagerState.currentPage
    val currentTag = listTag[selectedTabIndex]
    val scope = rememberCoroutineScope()
    var pageProgress by remember { mutableStateOf(1 to 1) }

    var endPos by remember { mutableStateOf(Offset.Zero) }


    Scaffold(topBar = {
        TopAppBar(title = { Text(currentTag.name.toCapital()) }, navigationIcon = {
            HBackIcon { goBack() }
        }, actions = {
            HPageProgress(pageProgress.first, pageProgress.second)
            HIcon(imageVector = Icons.Outlined.Search) { navToSearch() }
            if (tabs.isNotEmpty()) {
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        endPos = it.positionInRoot()
                    }) {
                    HIcon(
                        imageVector = Icons.Outlined.Tab,
                    ) { navToTabs() }
                    Text(
                        tabs.size.toString(),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        })
    }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            ScrollableTabRow(
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

                PageContent(
                    appViewModel,
                    tag = tag,
                    endPos = endPos,
                    navToCustomTag = navToCustomTag,
                    onPageChange = { currentPage, total ->
                        pageProgress = currentPage to total
                    }) { post ->
                    navToImages(post)
                }
            }
        }
    }
}

@Composable
fun PageContent(
    appViewModel: AppViewModel,
    tag: Tag,
    endPos: Offset,
    onPageChange: (Int, Int) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    onClick: (PostItem) -> Unit
) {
    val listFavorite by appViewModel.favoritePosts.collectAsState(initial = emptyList())
    val viewModel: PostsViewModel = viewModel(
        factory = PostsViewModelFactory(appViewModel.getCurrentSiteConfig(), tag),
        key = tag.name
    )
    val listState = rememberLazyListState()
    val uiState by viewModel.stateFlow.collectAsState()

    var startPos by remember { mutableStateOf(Offset.Zero) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.postItems, key = { it.url }) { post ->
                FavoriteCard(
                    postItem = post,
                    isFavorite = listFavorite.find { it.url == post.url } != null,
                    onTagClick = { tag ->
                        navToCustomTag(post, tag)
                    },
                    onAddToTabs = {
                        startPos = it
                        isAnimating = true
                        appViewModel.addTab(post)
                    },
                    setFavorite = { isFavorite ->
                        if (isFavorite)
                            appViewModel.addFavorite(post)
                        else
                            appViewModel.deleteFavorite(post)
                    }
                ) {
                    onClick(post)
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
        HGoTop(listState)


        AddToCartAnimation(
            isAnimating = isAnimating,
            startPosition = startPos,
            endPosition = endPos.copy(y = endPos.y - 100),
            onAnimationEnd = { isAnimating = false },
        )
    }
}
