package com.paulcoding.hviewer.extensions

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToEnd(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}
