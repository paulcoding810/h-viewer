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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.post.ImageList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TabsPage(goBack: () -> Unit, appViewModel: AppViewModel, siteConfigs: SiteConfigs) {
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())
    val reversedTabs by remember { derivedStateOf { tabs.reversed() } }
    val pagerState = rememberPagerState { reversedTabs.size }
    val scope = rememberCoroutineScope()
    val hostsMap by remember { derivedStateOf { siteConfigs.toHostsMap() } }

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
                key = { reversedTabs[it].url }
            ) { pageIndex ->
                val tab = reversedTabs[pageIndex]
                val siteConfig = tab.getSiteConfig(hostsMap)

                if (siteConfig != null)
                    ImageList(
                        tab.url,
                        siteConfig = siteConfig,
                        goBack = goBack,
                        bottomRowActions = {
                            BottomRowActions(pageIndex, scope, pagerState)
                        }
                    )
                else
                    Text(
                        "Site config not found for ${tab.url}",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red
                    )
            }
        }
    }
}

@Composable
internal fun BottomRowActions(pageIndex: Int, scope: CoroutineScope, pagerState: PagerState) {
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
}

