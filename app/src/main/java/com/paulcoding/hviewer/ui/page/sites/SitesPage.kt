package com.paulcoding.hviewer.ui.page.sites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesPage(
    isDevMode: Boolean,
    navToTopics: (site: String) -> Unit,
    goBack: () -> Unit,
    siteConfigs: SiteConfigs,
    navToSettings: () -> Unit,
    navToListScript: () -> Unit,
    refresh: () -> Unit,
    navToFavorite: () -> Unit,
) {
    val state = rememberPullToRefreshState()

//  TODO: check refreshing status
//   https://stackoverflow.com/questions/75293735/pullrefreshindicator-does-not-disappear-after-refreshing
    var refreshing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Sites") }, actions = {
            if (isDevMode)
                HIcon(Icons.Outlined.Edit) {
                    navToListScript()
                }
            HFavoriteIcon(isFavorite = false) {
                navToFavorite()
            }
            HIcon(Icons.Outlined.Settings, "Settings") { }
        })
    }) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = state,
            isRefreshing = refreshing,
            onRefresh = {
                scope.launch {
                    refreshing = true
                    refresh()
                    delay(100)
                    refreshing = false
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (siteConfigs.sites.keys.isEmpty()) {
                    HEmpty(title = "No sites found", message = "Add repo?") { navToSettings() }
                } else
                    siteConfigs.sites.keys.map { site ->
                        siteConfigs.sites[site]?.let { siteConfig ->
                            Site(
                                key = site,
                                site = siteConfig
                            ) {
                                navToTopics(site)
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun Site(site: SiteConfig, key: String, onClick: () -> Unit) {
    Box(modifier = Modifier.clickable {
        onClick()
    }) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            site.SiteIcon()
            Text(key)
        }
    }
}

