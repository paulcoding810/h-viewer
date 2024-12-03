package com.paulcoding.hviewer.ui.page

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.ui.favorite.FavoritePage
import com.paulcoding.hviewer.ui.page.post.PostPage
import com.paulcoding.hviewer.ui.page.posts.PostsPage
import com.paulcoding.hviewer.ui.page.search.SearchPage
import com.paulcoding.hviewer.ui.page.settings.SettingsPage
import com.paulcoding.hviewer.ui.page.sites.SitesPage

@Composable
fun AppEntry() {
    val navController = rememberNavController()

    val githubState by Github.stateFlow.collectAsState()
    val siteConfigs = githubState.siteConfigs ?: SiteConfigs()
    val appViewModel: AppViewModel = viewModel()

    fun navToImages(post: PostItem) {
        appViewModel.setCurrentPost(post)
        navController.navigate(Route.POST)
    }

    NavHost(navController, Route.SITES) {
        animatedComposable(Route.SITES) {
            SitesPage(siteConfigs = siteConfigs,
                refresh = { Github.refreshLocalConfigs() },
                navToTopics = { site ->
                    appViewModel.setSiteConfig(siteConfigs.sites[site]!!)
                    navController.navigate(Route.POSTS)
                }, navToSettings = {
                    navController.navigate(Route.SETTINGS)
                },
                navToFavorite = {
                    navController.navigate(Route.FAVORITE)
                },
                goBack = { navController.popBackStack() })
        }
        animatedComposable(Route.SETTINGS) {
            SettingsPage(goBack = { navController.popBackStack() })
        }
        animatedComposable(Route.POSTS) {
            PostsPage(
                appViewModel,
                navToImages = { post: PostItem ->
                    navToImages(post)
                },
                navToSearch = { navController.navigate(Route.SEARCH) },
                goBack = { navController.popBackStack() },
            )
        }
        animatedComposable(Route.POST) {
            PostPage(
                appViewModel,
                goBack = {
                    navController.popBackStack()
                })
        }
        animatedComposable(Route.SEARCH) {
            SearchPage(
                appViewModel = appViewModel,
                navToImages = { post: PostItem ->
                    navToImages(post)
                },
                goBack = { navController.popBackStack() },
            )
        }
        animatedComposable(Route.FAVORITE) {
            FavoritePage(
                appViewModel = appViewModel,
                navToImages = { post: PostItem ->
                    navToImages(post)
                },
                goBack = { navController.popBackStack() }
            )
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