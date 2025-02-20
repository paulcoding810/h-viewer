package com.paulcoding.hviewer.ui.page.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paulcoding.hviewer.ui.page.AppViewModel

@Composable
fun PostPage(appViewModel: AppViewModel, navToWebView: (String) -> Unit, goBack: () -> Unit) {
    val appState by appViewModel.stateFlow.collectAsState()
    val post = appState.post
    val siteConfig = appState.site.second

    ImageList(
        postUrl = post.url, siteConfig = siteConfig,
        goBack = goBack
    )
}
