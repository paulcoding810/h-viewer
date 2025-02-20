package com.paulcoding.hviewer.ui.page.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.page.AppViewModel

@Composable
fun PostPage(
    appViewModel: AppViewModel,
    hostMap: Map<String, SiteConfig>,
    navToWebView: (String) -> Unit,
    goBack: () -> Unit
) {
    val appState by appViewModel.stateFlow.collectAsState()
    val post = appState.post

    post.getSiteConfig(hostMap)?.let {
        ImageList(
            postUrl = post.url, siteConfig = it,
            goBack = goBack
        )
    }
}
