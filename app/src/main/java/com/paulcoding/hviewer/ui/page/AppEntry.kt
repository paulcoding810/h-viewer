package com.paulcoding.hviewer.ui.page

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paulcoding.hviewer.MainViewModel
import com.paulcoding.hviewer.ui.page.home.HomePage

@Composable
fun AppEntry(viewModal: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController, Route.HOME) {
        animatedComposable(Route.HOME) {
            HomePage(viewModal)
        }
    }
}



fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            fadeInWithBlur()
        },
        exitTransition = {
            fadeOutWithBlur()
        },
        popEnterTransition = {
            fadeInWithBlur()
        },
        popExitTransition = {
            fadeOutWithBlur()
        },
        content = content
    )
}


fun fadeInWithBlur(): EnterTransition {
    return fadeIn(animationSpec = tween(500))
}

fun fadeOutWithBlur(): ExitTransition {
    return fadeOut(animationSpec = tween(500))
}