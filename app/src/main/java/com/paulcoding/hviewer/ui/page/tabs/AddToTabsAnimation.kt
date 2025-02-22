package com.paulcoding.hviewer.ui.page.tabs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

internal const val animDuration = 600

@Composable
fun AddToCartAnimation(
    isAnimating: Boolean = true,
    modifier: Modifier = Modifier,
    startPosition: Offset,
    endPosition: Offset,
    onAnimationEnd: () -> Unit
) {
    val xAnim by animateFloatAsState(
        targetValue = if (isAnimating) endPosition.x else startPosition.x,
        animationSpec = tween(animDuration / 2)
    )

    val yAnim by animateFloatAsState(
        targetValue = if (isAnimating) endPosition.y else startPosition.y,
        animationSpec = tween(animDuration)
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (isAnimating) 0f else 1f,
        animationSpec = tween(animDuration, easing = FastOutSlowInEasing)
    )

    if (isAnimating) {
        LaunchedEffect(Unit) {
            delay(animDuration.toLong())
            onAnimationEnd()
        }
    }

    if (isAnimating) Box(
        modifier = modifier
            .offset { IntOffset(xAnim.toInt(), yAnim.toInt()) }
            .size(40.dp)
            .clip(CircleShape)
            .alpha(alpha = alphaAnim)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.Tab, contentDescription = "Tab", tint = MaterialTheme.colorScheme.primary
        )
    }
}
