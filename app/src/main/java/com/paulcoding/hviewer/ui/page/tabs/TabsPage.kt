package com.paulcoding.hviewer.ui.page.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.paulcoding.hviewer.database.DatabaseProvider
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.post.ImageList
import com.paulcoding.hviewer.ui.page.posts.InfoBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TabsPage(goBack: () -> Unit, appViewModel: AppViewModel, siteConfigs: SiteConfigs) {
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())
    val reversedTabs by remember { derivedStateOf { tabs.reversed() } }
    val pagerState = rememberPagerState { reversedTabs.size }
    val scope = rememberCoroutineScope()
    val hostsMap by remember { derivedStateOf { siteConfigs.toHostsMap() } }
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
        if (reversedTabs.isEmpty()) {
            HEmpty()
        } else {
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
                        tab.url,
                        siteConfig = siteConfig,
                        goBack = goBack,
                        bottomRowActions = {
                            BottomRowActions(
                                tab,
                                appViewModel,
                                pageIndex,
                                scope,
                                pagerState,
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
            InfoBottomSheet(
                visible = infoSheetVisible, postItem = reversedTabs[pagerState.currentPage],
                onDismissRequest = {
                    infoSheetVisible = false
                },
                onTagClick = {
                    infoSheetVisible = false
                    println(it)
                },
            )
        }
    }
}

@Composable
internal fun BottomRowActions(
    postItem: PostItem,
    appViewModel: AppViewModel,
    pageIndex: Int,
    scope: CoroutineScope,
    pagerState: PagerState,
    toggleBottomSheet: () -> Unit,
) {
    var favorite by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            favorite = DatabaseProvider.getInstance().postItemDao().isFavorite(postItem.url)
        }
    }

    HIcon(
        Icons.Outlined.ChevronLeft,
        size = 32,
        rounded = true,
        enabled = pageIndex > 0
    ) {
        scope.launch {
            pagerState.animateScrollToPage(
                pageIndex.dec()
            )
        }
    }
    Spacer(modifier = Modifier.width(16.dp))
    HIcon(
        Icons.Outlined.ChevronRight,
        size = 32,
        rounded = true,
        enabled = pageIndex < pagerState.pageCount - 1
    ) {
        scope.launch {
            pagerState.animateScrollToPage(
                pageIndex.inc()
            )
        }
    }

    Spacer(modifier = Modifier.width(16.dp))

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
            favorite = !favorite
        }
    }
}

