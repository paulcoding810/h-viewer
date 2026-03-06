package com.paulcoding.hviewer.ui.page.sites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesPage(
    viewModel: SitesViewModel = koinViewModel(),
    navToSite: (url: String, isSearch: Boolean) -> Unit,
    navToSettings: () -> Unit,
    navToHistory: () -> Unit,
    navToDownloads: () -> Unit,
    navToFavorite: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val effect by viewModel.effect.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effect) {
        when (val e = effect) {
            is SitesViewModel.Effect.UpdatedConfigs -> {
                snackbarHostState.showSnackbar(
                    message = "\uD83D\uDE80 Configs updated to version ${e.version}",
                    withDismissAction = true
                )
            }
            null -> {}
        }
        viewModel.dispatch(SitesViewModel.Actions.ConsumeEffect)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.sites)) }, actions = {
                HIcon(Icons.Outlined.Download) {
                    navToDownloads()
                }
                HIcon(Icons.Outlined.History) {
                    navToHistory()
                }
                HFavoriteIcon(isFavorite = false) {
                    navToFavorite()
                }
                HIcon(Icons.Outlined.Settings, "Settings") {
                    navToSettings()
                }
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = pullToRefreshState,
            isRefreshing = uiState.isLoading,
            onRefresh = {
                scope.launch {
                    viewModel.dispatch(SitesViewModel.Actions.LoadSites)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.siteConfigs == null) {
                    HEmpty(title = "No sites found", message = "Add repo?") { navToSettings() }
                } else {
                    val hostsMap = uiState.siteConfigs!!.toHostsMap()
                    hostsMap.keys.map { site ->
                        hostsMap[site]?.let { siteConfig ->
                            ListItem(
                                modifier = Modifier.clickable {
                                    navToSite(
                                        siteConfig.baseUrl,
                                        false
                                    )
                                },
                                headlineContent = { Text(siteConfig.name) },
                                leadingContent = {
                                    AsyncImage(
                                        model = "https://www.google.com/s2/favicons?sz=64&domain=${siteConfig.baseUrl}",
                                        contentDescription = siteConfig.baseUrl,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(R.mipmap.ic_launcher_foreground)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}