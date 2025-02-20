package com.paulcoding.hviewer.ui.page.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.alsoLog
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.post.ImageList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsPage(goBack: () -> Unit, appViewModel: AppViewModel, siteConfigs: SiteConfigs) {
    appViewModel.stateFlow
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())
    val pagerState = rememberPagerState { tabs.size }
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    val scope = rememberCoroutineScope()
    val hostsMap by remember { derivedStateOf { siteConfigs.toHostsMap() } }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tabs)) },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
            )
        }
    ) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            if (tabs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    HEmpty()
                }
            } else {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 0.dp,
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex == index,
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.outline,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(text = tab.getHost()) },
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { tabs[it].url }
                ) { pageIndex ->
                    val tab = tabs[pageIndex].alsoLog("tab")
                    val siteConfig = tab.getSiteConfig(hostsMap).alsoLog("siteConfig")

                    if (siteConfig != null)
                        ImageList(tab.url, siteConfig = siteConfig, goBack = goBack)
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
}

