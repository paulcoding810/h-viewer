package com.paulcoding.hviewer.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow

fun LazyListState.isScrolledToEnd(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}

@Composable
fun LazyListState.isScrollingUp(): State<Boolean> {
    return produceState(initialValue = true) {
        var previousIndex = firstVisibleItemIndex
        var previousOffset = firstVisibleItemScrollOffset

        snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                value = if (index != previousIndex) {
                    index < previousIndex
                } else {
                    offset < previousOffset
                }
                previousIndex = index
                previousOffset = offset
            }
    }
}
