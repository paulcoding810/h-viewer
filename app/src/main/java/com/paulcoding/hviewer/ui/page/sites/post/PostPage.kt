package com.paulcoding.hviewer.ui.page.sites.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.page.sites.composable.BottomRowActions
import com.paulcoding.hviewer.ui.page.sites.composable.InfoBottomSheet

@Composable
fun PostPage(
    viewModel: PostViewModel,
    navToCustomTag: (Tag) -> Unit,
    goBack: () -> Unit
) {
    val uiState by viewModel.stateFlow.collectAsState()
    var infoSheetVisible by remember { mutableStateOf(false) }

    ImageList(
        viewModel = viewModel,
        goBack = goBack,
        bottomRowActions = {
            BottomRowActions(
                postItem = uiState.postItem,
                onClickFavorite = { viewModel.toggleFavorite() },
                onClickInfo = { infoSheetVisible = true },
                onClickRemove = null,
                onClickRemoveAll = null,
            )
        }
    )

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
