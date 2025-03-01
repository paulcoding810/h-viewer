package com.paulcoding.hviewer.helper

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

class BasePaginationHelper(
    private val buffer: Int = 5,
    private val isLoading: () -> Boolean,
    private val hasMore: () -> Boolean,
    private val loadMore: () -> Unit
) {
    fun onScroll(lastVisibleIndex: Int, totalItemsCount: Int) {
        if (hasMore() && !isLoading() && lastVisibleIndex >= totalItemsCount - buffer) {
            loadMore()
        }
    }
}


@OptIn(FlowPreview::class)
@Composable
fun LoadMoreHandler(size: Int, listState: LazyListState, paginationHelper: BasePaginationHelper) {
    LaunchedEffect(listState, size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .debounce(200L)
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null) {
                    paginationHelper.onScroll(lastVisibleIndex, size)
                }
            }
    }
}