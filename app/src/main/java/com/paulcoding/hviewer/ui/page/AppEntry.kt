package com.paulcoding.hviewer.ui.page

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paulcoding.hviewer.helper.alsoLog
import com.paulcoding.hviewer.helper.readConfigFile
import com.paulcoding.hviewer.ui.model.SiteConfigs
import com.paulcoding.hviewer.ui.page.post.PostPage
import com.paulcoding.hviewer.ui.page.posts.PostsPage
import com.paulcoding.hviewer.ui.page.sites.SitesPage
import com.paulcoding.hviewer.ui.page.topics.TopicsPage
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppEntry() {
    val navController = rememberNavController()

    val context = LocalContext.current
    var siteConfigs by remember { mutableStateOf(SiteConfigs()) }

    LaunchedEffect(Unit) {
        context.readConfigFile<SiteConfigs>().alsoLog("siteConfigs")
            .onSuccess {
                siteConfigs = it
            }
            .onFailure {

            }
    }

    NavHost(navController, Route.SITES) {
        animatedComposable(Route.SITES) {
            SitesPage(siteConfigs = siteConfigs, navToTopics = { site ->
                navController.navigate("${Route.TOPICS}/$site")
            }, goBack = { navController.popBackStack() })
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
                topic = topic,
                navToImages = { postUrl: String ->
                    navController.navigate(
                        "${Route.POST}/${site}/${
                            URLEncoder.encode(postUrl, StandardCharsets.UTF_8.toString())
                        }"
                    )
                },
                goBack = { navController.popBackStack() })
        }
        animatedComposable("${Route.POST}/{site}/{postUrl}") { backStackEntry ->
            val postUrl = backStackEntry.arguments?.getString("postUrl") ?: ""
            val site = backStackEntry.arguments?.getString("site") ?: ""
            val decodedUrl = URLDecoder.decode(postUrl, StandardCharsets.UTF_8.toString())
            PostPage(
                siteConfig = siteConfigs.sites[site]!!, postUrl = decodedUrl, goBack = {
                    navController.popBackStack()
                })
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