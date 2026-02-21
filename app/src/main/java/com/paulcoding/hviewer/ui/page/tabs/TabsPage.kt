package com.paulcoding.hviewer.ui.page.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.sites.composable.InfoBottomSheet
import com.paulcoding.hviewer.ui.page.sites.post.ImageList
import kotlinx.coroutines.launch

@Composable
fun TabsPage(
    viewModel: TabsViewModel,
    goBack: () -> Unit,
    navToCustomTag: (Tag) -> Unit,
) {
    val reversedTabs by viewModel.tabsWithFavorite.collectAsState(initial = listOf())

    val pagerState = rememberPagerState { reversedTabs.size }

    val scope = rememberCoroutineScope()
    var showInfoSheet by remember { mutableStateOf<PostItem?>(null) }

    BackHandler {
        if (pagerState.currentPage > 0) {
            scope.launch {
                pagerState.animateScrollToPage(
                    pagerState.currentPage.dec()
                )
            }
        } else {
            goBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 2,
            key = { reversedTabs[it].first.url }
        ) { pageIndex ->
            val (tab, delegate) = reversedTabs[pageIndex]

            ImageList(
                delegate,
                goBack = goBack,
                bottomRowActions = {
                    BottomRowActions(
                        postItem = tab,
                        removeTab = {
                            viewModel.removeTab(tab)
                            if (pagerState.pageCount == 1) {
                                goBack()
                            }
                        },
                        toggleFavorite = viewModel::toggleFavorite,
                        toggleBottomSheet = {
                            showInfoSheet = tab
                        })
                }
            )
        }
    }
    showInfoSheet?.let { currentPost ->
        InfoBottomSheet(
            visible = true,
            postItem = currentPost,
            onDismissRequest = {
                showInfoSheet = null
            },
            onTagClick = {
                showInfoSheet = null
                navToCustomTag(it)
            },
        )
    }
}

@Composable
internal fun BottomRowActions(
    postItem: PostItem,
    removeTab: () -> Unit,
    toggleFavorite: (PostItem) -> Unit,
    toggleBottomSheet: () -> Unit,
) {

    HIcon(
        Icons.Outlined.Info,
        size = 32,
        rounded = true
    ) {
        toggleBottomSheet()
    }

    Spacer(modifier = Modifier.width(16.dp))

    HFavoriteIcon(isFavorite = postItem.favorite, rounded = true) {
        toggleFavorite(postItem)
    }

    Spacer(modifier = Modifier.width(16.dp))

    HIcon(
        Icons.Outlined.Close,
        size = 32,
        rounded = true,
        onClick = removeTab
    )
}

