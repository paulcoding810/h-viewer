package com.paulcoding.hviewer.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow

/**
 * Tracks whether the list is scrolling upward.
 * Returns true when scrolling up, false when scrolling down or stationary.
 * Uses throttling to prevent excessive state updates.
 */
@Composable
fun LazyListState.isScrollingUpwards(): State<Boolean> {
    return produceState(initialValue = true) {
        var previousIndex = firstVisibleItemIndex
        var previousOffset = firstVisibleItemScrollOffset
        var lastEmissionTime = 0L
        val throttleInterval = 100L

        snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastEmissionTime >= throttleInterval) {
                    value = if (index != previousIndex) {
                        index < previousIndex
                    } else {
                        offset < previousOffset
                    }
                    previousIndex = index
                    previousOffset = offset
                    lastEmissionTime = currentTime
                }
            }
    }
}
