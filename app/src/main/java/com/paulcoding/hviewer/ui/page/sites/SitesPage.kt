package com.paulcoding.hviewer.ui.page.sites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefreshIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.ui.icon.EditIcon
import com.paulcoding.hviewer.ui.icon.SettingsIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesPage(
    navToTopics: (site: String) -> Unit,
    goBack: () -> Unit,
    siteConfigs: SiteConfigs,
    navToSettings: () -> Unit,
    navToEditor: (site: String) -> Unit,
    refresh: () -> Unit,
) {
    val state = rememberPullToRefreshState()

//  TODO: check refreshing status
//   https://stackoverflow.com/questions/75293735/pullrefreshindicator-does-not-disappear-after-refreshing
    var refreshing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Sites") }, actions = {
            IconButton(onClick = navToSettings) {
                Icon(SettingsIcon, "Settings")
            }
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
            Box(
                modifier = Modifier.pullToRefreshIndicator(
                    isRefreshing = false,
                    state = rememberPullToRefreshState(),
                )
            ) {}
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (siteConfigs.sites.keys.isEmpty()) {
                    Empty(navToSettings)
                } else
                    siteConfigs.sites.keys.map { site ->
                        siteConfigs.sites[site]?.let { siteConfig ->
                            Site(
                                key = site,
                                site = siteConfig,
                                onEdit = { navToEditor(site) }
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
fun Site(site: SiteConfig, key: String, onEdit: () -> Unit, onClick: () -> Unit) {
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
            Text(key, modifier = Modifier.weight(1f))
            IconButton(onClick = onEdit) {
                Icon(EditIcon, "Edit")
            }
        }
    }
}

@Composable
private fun Empty(navToSettings: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(composition)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("No sites found")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Add repo?",
                modifier = Modifier.clickable { navToSettings() },
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
