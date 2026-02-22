package com.paulcoding.hviewer.ui.page.sites.post

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.sites.composable.InfoBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostPage(
    viewModel: PostViewModel,
    navToWebView: (String) -> Unit,
    navToCustomTag: (Tag) -> Unit,
    goBack: () -> Unit
) {
    val uiState by viewModel.stateFlow.collectAsState()
    var infoSheetVisible by remember { mutableStateOf(false) }

    ImageList(
        viewModel = viewModel,
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

            HFavoriteIcon(isFavorite = uiState.isFavorite, rounded = true) {
                viewModel.toggleFavorite()
            }
        })

    InfoBottomSheet(
        visible = infoSheetVisible,
        postItem = uiState.postItem,
        onDismissRequest = {
            infoSheetVisible = false
        },
        onTagClick = { tag ->
            infoSheetVisible = false
            navToCustomTag(tag)
        },
    )
}
