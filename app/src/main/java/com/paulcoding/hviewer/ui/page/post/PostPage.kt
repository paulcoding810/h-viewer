package com.paulcoding.hviewer.ui.page.post

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.LocalHostsMap
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.InfoBottomSheet
import kotlinx.coroutines.launch

@Composable
fun PostPage(
    appViewModel: AppViewModel,
    navToWebView: (String) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    goBack: () -> Unit
) {
    val appState by appViewModel.stateFlow.collectAsState()
    val post = remember { appState.post }
    val siteConfig = appViewModel.getCurrentSiteConfig()
    val favorite by appViewModel.postFavorite(post.url).collectAsState(false)
    val hostsMap = LocalHostsMap.current
    var infoSheetVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ImageList(
        post = post,
        siteConfig = siteConfig,
        goBack = goBack,
        bottomRowActions = {
            HIcon(
                Icons.Outlined.Info,
                size = 32,
                rounded = true
            ) {
                infoSheetVisible = true
            }

            Spacer(modifier = Modifier.width(16.dp))

            HFavoriteIcon(isFavorite = favorite, rounded = true) {
                scope.launch {
                    if (!favorite)
                        appViewModel.addFavorite(postItem = post)
                    else
                        appViewModel.deleteFavorite(post)
                }
            }
        })

    InfoBottomSheet(
        visible = infoSheetVisible,
        postItem = post,
        onDismissRequest = {
            infoSheetVisible = false
        },
        onTagClick = { tag ->
            infoSheetVisible = false
            navToCustomTag(post, tag)
        },
    )
}
