package com.paulcoding.hviewer.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.extensions.isScrollingUpwards
import kotlinx.coroutines.launch

@Composable
fun BoxScope.HGoTop(listState: LazyListState) {
    val scope = rememberCoroutineScope()

    val isScrollingUpwards by listState.isScrollingUpwards()

    val translationY by animateDpAsState(
        targetValue = if (isScrollingUpwards) 0.dp else 100.dp,
        animationSpec = tween(200)
    )

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset {
                IntOffset(x = 0, y = translationY.roundToPx())
            }
            .padding(16.dp)
    ) {
        FilledTonalIconButton(
            onClick = {
                scope.launch {
                    listState.animateScrollToItem(0, 0)
                }
            },
        ) {
            Icon(Icons.Outlined.KeyboardArrowUp, "Go to Top")
        }
    }
}