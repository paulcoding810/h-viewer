package com.paulcoding.hviewer.ui.page.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.LocalHostsMap
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.post.ImageList
import com.paulcoding.hviewer.ui.page.posts.InfoBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TabsPage(
    goBack: () -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    appViewModel: AppViewModel,
) {
    val hostsMap = LocalHostsMap.current
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())
    val reversedTabs by remember { derivedStateOf { tabs.reversed() } }
    val pagerState = rememberPagerState { reversedTabs.size }

    val scope = rememberCoroutineScope()
    var infoSheetVisible by remember { mutableStateOf(false) }

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
            val siteConfig = tab.getSiteConfig(hostsMap)

            if (siteConfig != null) {
                ImageList(
                    tab,
                    siteConfig = siteConfig,
                    goBack = goBack,
                    bottomRowActions = {
                        BottomRowActions(
                            tab,
                            appViewModel,
                            pageIndex,
                            scope,
                            toggleBottomSheet = {
                                infoSheetVisible = !infoSheetVisible
                            })
                    }
                )
            } else
                Text(
                    "Site config not found for ${tab.url}",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
        }
        reversedTabs.getOrNull(pagerState.currentPage)?.let { currentPost ->
            InfoBottomSheet(
                visible = infoSheetVisible,
                postItem = currentPost,
                onDismissRequest = {
                    infoSheetVisible = false
                },
                onTagClick = {
                    infoSheetVisible = false
                    navToCustomTag(currentPost, it)
                },
            )
        }
    }
}

@Composable
internal fun BottomRowActions(
    postItem: PostItem,
    appViewModel: AppViewModel,
    totalPage: Int,
    scope: CoroutineScope,
    toggleBottomSheet: () -> Unit,
) {
    val favorite by appViewModel.postFavorite(postItem.url).collectAsState(false)

    HIcon(
        Icons.Outlined.Info,
        size = 32,
        rounded = true
    ) {
        toggleBottomSheet()
    }

    Spacer(modifier = Modifier.width(16.dp))

    HFavoriteIcon(isFavorite = favorite, rounded = true) {
        scope.launch {
            if (!favorite)
                appViewModel.addFavorite(postItem = postItem)
            else
                appViewModel.deleteFavorite(postItem)
        }
    }

    Spacer(modifier = Modifier.width(16.dp))

    if (totalPage > 1) {
        HIcon(
            Icons.Outlined.Close,
            size = 32,
            rounded = true
        ) {
            appViewModel.removeTab(postItem)
        }
    }
}

