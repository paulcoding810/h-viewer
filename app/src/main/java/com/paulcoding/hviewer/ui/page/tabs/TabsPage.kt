package com.paulcoding.hviewer.ui.page.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.page.sites.composable.BottomRowActions
import com.paulcoding.hviewer.ui.page.sites.composable.InfoBottomSheet
import com.paulcoding.hviewer.ui.page.sites.post.ImageList
import com.paulcoding.hviewer.ui.page.sites.post.PostViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TabsPage(
    viewModel: TabsViewModel,
    viewModelStoreOwner: NavBackStackEntry,
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
            key = { reversedTabs[it].url }
        ) { pageIndex ->
            val tab = reversedTabs[pageIndex]

            val postViewModel = koinViewModel<PostViewModel>(
                viewModelStoreOwner = viewModelStoreOwner,
                key = tab.url,
                parameters = { parametersOf(tab, false) }
            )

            ImageList(
                postViewModel,
                goBack = goBack,
                bottomRowActions = {
                    BottomRowActions(
                        postItem = tab,
                        onClickRemove = {
                            viewModel.removeTab(tab)
                            if (pagerState.pageCount == 1) {
                                goBack()
                            }
                        },
                        onClickRemoveAll = {
                            goBack()
                            viewModel.clearTabs()
                        },
                        onClickFavorite = viewModel::toggleFavorite,
                        onClickInfo = {
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

