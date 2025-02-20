package com.paulcoding.hviewer.ui.page

import android.content.Intent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.network.Github
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.favorite.FavoritePage
import com.paulcoding.hviewer.ui.page.editor.EditorPage
import com.paulcoding.hviewer.ui.page.editor.ListScriptPage
import com.paulcoding.hviewer.ui.page.history.HistoryPage
import com.paulcoding.hviewer.ui.page.lock.LockPage
import com.paulcoding.hviewer.ui.page.post.PostPage
import com.paulcoding.hviewer.ui.page.posts.CustomTagPage
import com.paulcoding.hviewer.ui.page.posts.PostsPage
import com.paulcoding.hviewer.ui.page.search.SearchPage
import com.paulcoding.hviewer.ui.page.settings.SettingsPage
import com.paulcoding.hviewer.ui.page.sites.SitesPage
import com.paulcoding.hviewer.ui.page.tabs.TabsPage
import com.paulcoding.hviewer.ui.page.web.WebPage

@Composable
fun AppEntry(intent: Intent?) {
    val navController = rememberNavController()

    val githubState by Github.stateFlow.collectAsState()
    val siteConfigs = githubState.siteConfigs ?: SiteConfigs()
    val appViewModel: AppViewModel = viewModel()
    val appState by appViewModel.stateFlow.collectAsState()

    fun navToImages(post: PostItem) {
        appViewModel.setCurrentPost(post)
        appViewModel.addHistory(post)
        navController.navigate(Route.POST)
    }

    fun navToCustomTag(tag: Tag) {
        appViewModel.setCurrentTag(tag)
        navController.navigate(Route.CUSTOM_TAG)
    }

    val startDestination =
        remember { if (Preferences.pin.isNotEmpty()) Route.LOCK else Route.SITES }

    // handle intent
    val updatedIntent by rememberUpdatedState(intent)

    fun handleIntentUrl(url: String) {
        val postItem = PostItem(url = url)
        navToImages(postItem)
    }

    LaunchedEffect(updatedIntent) {
        updatedIntent?.apply {
            when (action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == type) {
                        getStringExtra(Intent.EXTRA_TEXT)?.let {
                            handleIntentUrl(it)
                        }
                    }
                }

                Intent.ACTION_VIEW -> {
                    handleIntentUrl(data.toString())
                }

                else -> {
                }
            }
        }
    }

    NavHost(navController, startDestination = startDestination) {
        animatedComposable(Route.SITES) {
            SitesPage(
                isDevMode = appState.isDevMode,
                siteConfigs = siteConfigs,
                refresh = { Github.refreshLocalConfigs() },
                navToTopics = { site ->
                    appViewModel.setSiteConfig(site, siteConfigs.sites[site]!!)
                    navController.navigate(Route.POSTS)
                }, navToSettings = {
                    navController.navigate(Route.SETTINGS)
                },
                navToFavorite = {
                    navController.navigate(Route.FAVORITE)
                },
                navToHistory = {
                    navController.navigate(Route.HISTORY)
                },
                goBack = { navController.popBackStack() })
        }
        animatedComposable(Route.SETTINGS) {
            SettingsPage(appViewModel = appViewModel,
                navToListScript = {
                    navController.navigate(Route.LIST_SCRIPT + "/script")
                },
                navToListCrashLog = {
                    navController.navigate(Route.LIST_SCRIPT + "/crash_log")
                },
                onLockEnabled = {
                    navController.navigate(Route.LOCK) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                }, goBack = { navController.popBackStack() })
        }
        animatedComposable(Route.POSTS) {
            PostsPage(
                appViewModel,
                navToImages = { post: PostItem ->
                    navToImages(post)
                },
                navToSearch = { navController.navigate(Route.SEARCH) },
                navToCustomTag = { navToCustomTag(it) },
                navToTabs = { navController.navigate(Route.TABS) },
                goBack = { navController.popBackStack() },
            )
        }
        animatedComposable(Route.CUSTOM_TAG) {
            CustomTagPage(
                appViewModel,
                navToCustomTag = { navToCustomTag(it) },
                goBack = { navController.popBackStack() }
            ) {
                navToImages(it)
            }
        }
        animatedComposable(Route.POST) {
            PostPage(
                appViewModel,
                navToWebView = {
                    appViewModel.setWebViewUrl(it)
                    navController.navigate(Route.WEBVIEW)
                },
                hostMap = siteConfigs.toHostsMap(),
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
                navToCustomTag = { navToCustomTag(it) },
                goBack = { navController.popBackStack() },
            )
        }
        animatedComposable(Route.FAVORITE) {
            FavoritePage(
                appViewModel = appViewModel,
                navToImages = { post: PostItem ->
                    appViewModel.setSiteConfig(post.site, siteConfigs.sites[post.site]!!)
                    navToImages(post)
                },
                navToCustomTag = { post, tag ->
                    appViewModel.setSiteConfig(post.site, siteConfigs.sites[post.site]!!)
                    navToCustomTag(tag)
                },
                goBack = { navController.popBackStack() }
            )
        }
        animatedComposable(Route.LIST_SCRIPT + "/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")!!

            ListScriptPage(
                appViewModel = appViewModel,
                type = type,
                goBack = { navController.popBackStack() },
                navToEditor = {
                    navController.navigate(Route.EDITOR + "/$type" + "/$it")
                })
        }
        animatedComposable(Route.EDITOR + "/{type}" + "/{scriptFile}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")!!
            val scriptFile = backStackEntry.arguments?.getString("scriptFile")!!

            EditorPage(
                appViewModel = appViewModel,
                type = type,
                scriptFile = scriptFile,
                goBack = { navController.popBackStack() })
        }
        animatedComposable(Route.LOCK) {
            LockPage(onUnlocked = {
                navController.navigate(Route.SITES)
                {
                    popUpTo(Route.LOCK) {
                        inclusive = true
                    }
                }
            })
        }
        animatedComposable(Route.HISTORY) {
            HistoryPage(
                goBack = { navController.popBackStack() }, appViewModel = appViewModel,
                navToImages = { post: PostItem ->
                    appViewModel.setSiteConfig(post.site, siteConfigs.sites[post.site]!!)
                    navToImages(post)
                },
                navToCustomTag = { post, tag ->
                    appViewModel.setSiteConfig(post.site, siteConfigs.sites[post.site]!!)
                    navToCustomTag(tag)
                },
                deleteHistory = appViewModel::deleteHistory
            )
        }
        animatedComposable(Route.WEBVIEW) {
            val url = appViewModel.getWebViewUrl()
            WebPage(goBack = { navController.popBackStack() }, url = url)
        }
        animatedComposable(Route.TABS) {
            TabsPage(goBack = {
                navController.popBackStack()
                appViewModel.clearTabs()
            }, appViewModel = appViewModel, siteConfigs = siteConfigs)
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