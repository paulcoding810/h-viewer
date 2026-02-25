package com.paulcoding.hviewer.ui.page.sites.post

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.isScrollingUp
import com.paulcoding.hviewer.helper.BasePaginationHelper
import com.paulcoding.hviewer.helper.LoadMoreHandler
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HideSystemBar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun ImageList(
    viewModel: PostViewModel,
    goBack: () -> Unit,
    bottomRowActions: @Composable (() -> Unit) = {},
) {
    val uiState by viewModel.stateFlow.collectAsState()

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.scrollIndex,
        initialFirstVisibleItemScrollOffset = uiState.scrollOffset
    )
    val isScrollingUp by listState.isScrollingUp()

    // Throttle scroll position updates to avoid excessive updates
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .debounce(200)
            .collect { (index, offset) ->
                viewModel.updateScrollIndex(index, offset)
            }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.getImagesAtLaunch) {
            viewModel.getImages()
        }
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

    ImageList(
        listState = listState,
        systemBarVisible = isScrollingUp,
        images = uiState.images.toList(),
        isLoading = uiState.isLoading,
        currentPage = uiState.postPage,
        totalPage = uiState.postTotalPage,
        bottomRowActions = bottomRowActions,
        goBack = goBack,
    )
}

@Composable
private fun ImageList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    images: List<String> = emptyList(),
    bottomRowActions: @Composable (() -> Unit) = {},
    systemBarVisible: Boolean = true,
    isLoading: Boolean = false,
    currentPage: Int = 0,
    totalPage: Int = 0,
    goBack: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    var selectedImage by remember { mutableStateOf<Int?>(null) }
    var selectedImageOffset = remember<Offset?> { null }

    val translationY by animateDpAsState(
        targetValue = if (!systemBarVisible) (-100).dp else 0.dp,
        animationSpec = tween(200)
    )

    HideSystemBar(isHidden = !systemBarVisible, onBack = goBack)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(images, key = { _, image -> image }) { index, image ->
                PostImage(
                    url = image,
                    onDoubleTap = {
                        selectedImage = index
                    },
                    onTap = {},
                )
            }
            if (isLoading) {
                item {
                    HLoading(modifier.systemBarsPadding())
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
            HIcon(Icons.AutoMirrored.Outlined.ArrowBack, rounded = true, onClick = goBack)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(2.dp)
            ) {
                Text(
                    text = "${currentPage}/${totalPage}",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
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
        selectedImage?.let { index ->
            ImageModal(
                url = images[index],
                onImageFirstOffset = {
                    selectedImageOffset = it
                },
                dismiss = {
                    scope.launch {
                        val offsetY = selectedImageOffset?.y?.toInt() ?: 0
                        listState.animateScrollToItem(index, -offsetY)
                        selectedImageOffset = null
                        selectedImage = null
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewImageList() {
    ImageList(
        images = List(20) { "https://picsum.photos/id/$it/200/300" },
        isLoading = false,
        systemBarVisible = true,
        currentPage = 1,
        totalPage = 20,
    )
}