package com.paulcoding.hviewer.helper

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

fun fadeInWithBlur(): EnterTransition {
    return fadeIn(animationSpec = tween(500))
}

fun fadeOutWithBlur(): ExitTransition {
    return fadeOut(animationSpec = tween(500))
}