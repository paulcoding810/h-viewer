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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.ui.page.post.PostPage
import com.paulcoding.hviewer.ui.page.posts.PostsPage
import com.paulcoding.hviewer.ui.page.settings.SettingsPage
import com.paulcoding.hviewer.ui.page.sites.SitesPage
import com.paulcoding.hviewer.ui.page.topics.TopicsPage
import com.paulcoding.hviewer.ui.page.webeditor.WebEditorPage
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppEntry() {
    val navController = rememberNavController()

    val githubState by Github.stateFlow.collectAsState()
    val siteConfigs = githubState.siteConfigs ?: SiteConfigs()

    NavHost(navController, Route.SITES) {
        animatedComposable(Route.SITES) {
            SitesPage(siteConfigs = siteConfigs,
                refresh = { Github.refreshLocalConfigs() },
                navToTopics = { site ->
                    val firstTopic = siteConfigs.sites[site]?.tags?.keys?.first()
                    if (firstTopic != null) {
                        navController.navigate("${Route.POSTS}/$site/$firstTopic")
                    } else {
                        navController.navigate("${Route.TOPICS}/$site")
                    }
                }, navToSettings = {
                    navController.navigate(Route.SETTINGS)
                },
                navToEditor = { site ->
                    navController.navigate("${Route.EDITOR}/$site")
                },
                goBack = { navController.popBackStack() })
        }
        animatedComposable(Route.SETTINGS) {
            SettingsPage(goBack = { navController.popBackStack() })
        }
        animatedComposable("${Route.TOPICS}/{site}") { backStackEntry ->
            val site = backStackEntry.arguments?.getString("site") ?: ""
            val siteConfig = siteConfigs.sites[site]

            if (siteConfig != null)
                TopicsPage(
                    siteConfig = siteConfig,
                    navToTopic = { topic ->
                        navController.navigate(
                            "${Route.POSTS}/${site}/$topic"
                        )
                    },
                    goBack = { navController.popBackStack() })
        }
        animatedComposable("${Route.POSTS}/{site}/{topic}") { backStackEntry ->
            val topic = backStackEntry.arguments?.getString("topic") ?: ""
            val site = backStackEntry.arguments?.getString("site") ?: ""
            val siteConfig = siteConfigs.sites[site]!!

            PostsPage(
                siteConfig = siteConfig,
                initialTopic = topic,
                navToImages = { postUrl: String ->
                    navController.navigate(
                        "${Route.POST}/${site}/${topic}/${
                            URLEncoder.encode(postUrl, StandardCharsets.UTF_8.toString())
                        }"
                    )
                },
                goBack = { navController.popBackStack() })
        }
        animatedComposable("${Route.POST}/{site}/{topic}/{postUrl}") { backStackEntry ->
            val postUrl = backStackEntry.arguments?.getString("postUrl") ?: ""
            val site = backStackEntry.arguments?.getString("site") ?: ""
            val decodedUrl = URLDecoder.decode(postUrl, StandardCharsets.UTF_8.toString())
            PostPage(
                siteConfig = siteConfigs.sites[site]!!, postUrl = decodedUrl, goBack = {
                    navController.popBackStack()
                })
        }
        animatedComposable("${Route.EDITOR}/{site}") { backStackEntry ->
            val site = backStackEntry.arguments?.getString("site") ?: ""

            WebEditorPage(
                goBack = { navController.popBackStack() },
                siteConfig = siteConfigs.sites[site]!!
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