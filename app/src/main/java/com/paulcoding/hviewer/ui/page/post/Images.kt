package com.paulcoding.hviewer.ui.page.post

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.helper.BasePaginationHelper
import com.paulcoding.hviewer.helper.LoadMoreHandler
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.SystemBar
import kotlinx.coroutines.launch

@Composable
fun ImageList(
    post: PostItem,
    siteConfig: SiteConfig,
    goBack: () -> Unit,
    bottomRowActions: @Composable (RowScope.() -> Unit) = {},
) {
    val viewModel: PostViewModel = viewModel(
        key = post.url,
        factory = PostViewModelFactory(post.url, siteConfig = siteConfig)
    )

    val uiState by viewModel.stateFlow.collectAsState()
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val translationY by animateDpAsState(
        targetValue = if (uiState.isSystemBarHidden) (-100).dp else 0.dp,
        animationSpec = tween(200)
    )


    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getImages()
    }

    val paginationHelper = remember {
        BasePaginationHelper(
            buffer = 5,
            isLoading = { uiState.isLoading },
            hasMore = viewModel::canLoadMorePostData,
            loadMore = viewModel::getNextImages
        )
    }

    LoadMoreHandler(uiState.images.size, listState, paginationHelper)

    SystemBar(uiState.isSystemBarHidden)

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            viewModel.toggleSystemBarHidden()
        }) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.images.toList(), key = { it }) { image ->
                PostImage(
                    url = image,
                    onDoubleTap = {
                        selectedImage = image
                    },
                    onTap = {
                        viewModel.toggleSystemBarHidden()
                    },
                )
            }
            if (uiState.isLoading)
                item {
                    Box(
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        HLoading()
                    }
                }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(x = 0, y = translationY.roundToPx())
                }
                .padding(16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HIcon(Icons.AutoMirrored.Outlined.ArrowBack, rounded = true) { goBack() }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(x = 0, y = -translationY.roundToPx())
                }
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bottomRowActions()
            Spacer(modifier = Modifier.weight(1f))
            HIcon(
                Icons.Outlined.KeyboardArrowUp,
                size = 32,
                tint = MaterialTheme.colorScheme.primary,
                rounded = true
            ) {
                scope.launch {
                    listState.animateScrollToItem(0, 0)
                }
            }
        }

        if (selectedImage != null) {
            ImageModal(url = selectedImage!!) {
                selectedImage = null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.4f))
                .align(Alignment.BottomEnd)
                .padding(horizontal = 28.dp),
        ) {
            Text(
                "${uiState.postPage}/${uiState.postTotalPage}",
                modifier = Modifier.align(Alignment.BottomEnd),
                fontSize = 10.sp,
                maxLines = 1,
                color = Color.White,
            )
        }

    }
}