package com.paulcoding.hviewer.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.extensions.isScrollingUp
import com.paulcoding.hviewer.ui.page.fadeInWithBlur
import com.paulcoding.hviewer.ui.page.fadeOutWithBlur
import kotlinx.coroutines.launch

@Composable
fun BoxScope.HGoTop(listState: LazyListState) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        listState.isScrollingUp().value,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        enter = fadeInWithBlur(),
        exit = fadeOutWithBlur(),
    ) {
        FloatingActionButton(
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