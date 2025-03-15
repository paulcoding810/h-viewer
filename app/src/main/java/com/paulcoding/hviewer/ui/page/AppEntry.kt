package com.paulcoding.hviewer.ui.page

import android.content.Intent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.LocalHostsMap
import com.paulcoding.hviewer.ui.favorite.FavoritePage
import com.paulcoding.hviewer.ui.page.downloads.DownloadsPage
import com.paulcoding.hviewer.ui.page.editor.EditorPage
import com.paulcoding.hviewer.ui.page.editor.ListScriptPage
import com.paulcoding.hviewer.ui.page.history.HistoryPage
import com.paulcoding.hviewer.ui.page.post.PostPage
import com.paulcoding.hviewer.ui.page.posts.CustomTagPage
import com.paulcoding.hviewer.ui.page.posts.PostsPage
import com.paulcoding.hviewer.ui.page.search.SearchPage
import com.paulcoding.hviewer.ui.page.settings.SettingsPage
import com.paulcoding.hviewer.ui.page.sites.SitesPage
import com.paulcoding.hviewer.ui.page.tabs.TabsPage
import com.paulcoding.hviewer.ui.page.web.WebPage

@Composable
fun AppEntry(intent: Intent?, appViewModel: AppViewModel) {
    val navController = rememberNavController()
    val appState by appViewModel.stateFlow.collectAsState()
    val hostsMap by appViewModel.hostsMap.collectAsState()

    val context = LocalContext.current

    fun navToImages(post: PostItem) {
        appViewModel.setCurrentPost(post)
        appViewModel.addHistory(post)
        navController.navigate(Route.POST)
    }

    fun navToCustomTag(post: PostItem, tag: Tag) {
        appViewModel.setCurrentPost(post)
        navController.currentBackStackEntry?.savedStateHandle?.set("tag", tag)
        navController.navigate(Route.CUSTOM_TAG)
    }

    // handle intent
    val updatedIntent by rememberUpdatedState(intent)

    fun handleIntentUrl(url: String) {
        val postItem = PostItem(url = url)
        if (postItem.getSiteConfig(hostsMap) != null) {
            navToImages(postItem)
        } else {
            makeToast(context.getString(R.string.invalid_url, url))
        }
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
                    // TODO: why deeplink not working
                    if (data.toString().startsWith("hviewer://")) {
                        val route = data.toString().substringAfter("hviewer://")
                        navController.navigate(route)
                    } else {
                        handleIntentUrl(data.toString())
                    }
                }

                else -> {
                }
            }
        }
    }
    CompositionLocalProvider(LocalHostsMap provides hostsMap) {
        NavHost(navController, startDestination = Route.SITES) {
            animatedComposable(Route.SITES) {
                SitesPage(
                    isDevMode = appState.isDevMode,
                    refresh = {
                        if (BuildConfig.DEBUG)
                            appViewModel.refreshLocalConfigs()
                        else
                            appViewModel.checkVersionOrUpdate(
                                Preferences.getRemote(),
                                onUpdate = { state ->
                                    makeToast(state.getToastMessage())
                                })
                    },
                    navToTopics = { siteConfig ->
                        appViewModel.setCurrentPost(PostItem(siteConfig.baseUrl))
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
                    navToDownloads = {
                        navController.navigate("downloads/")
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
                    goBack = { navController.popBackStack() })
            }
            animatedComposable(Route.POSTS) {
                PostsPage(
                    appViewModel,
                    navToImages = { post: PostItem ->
                        navToImages(post)
                    },
                    navToSearch = { navController.navigate(Route.SEARCH) },
                    navToCustomTag = { postItem, tag -> navToCustomTag(postItem, tag) },
                    navToTabs = { navController.navigate(Route.TABS) },
                    goBack = { navController.popBackStack() },
                )
            }
            animatedComposable(Route.CUSTOM_TAG) {
                val tag = navController.previousBackStackEntry?.savedStateHandle?.get<Tag>("tag")
                if (tag != null)
                    CustomTagPage(
                        appViewModel,
                        tag = tag,
                        navToCustomTag = { postItem, newTag -> navToCustomTag(postItem, newTag) },
                        navToTabs = { navController.navigate(Route.TABS) },
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
                    navToCustomTag = { postItem, tag -> navToCustomTag(postItem, tag) },
                    navToTabs = { navController.navigate(Route.TABS) },
                    goBack = { navController.popBackStack() },
                )
            }
            animatedComposable(Route.FAVORITE) {
                FavoritePage(
                    appViewModel = appViewModel,
                    navToImages = { post: PostItem ->
                        navToImages(post)
                    },
                    navToCustomTag = { post, tag ->
                        navToCustomTag(post, tag)
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
            animatedComposable(Route.HISTORY) {
                HistoryPage(
                    goBack = { navController.popBackStack() }, appViewModel = appViewModel,
                    navToImages = { post: PostItem ->
                        navToImages(post)
                    },
                    navToCustomTag = { post, tag ->
                        navToCustomTag(post, tag)
                    },
                    clearHistory = appViewModel::clearHistory,
                    deleteHistory = appViewModel::deleteHistory,
                )
            }
            animatedComposable(Route.WEBVIEW) {
                val url = appViewModel.getWebViewUrl()
                WebPage(goBack = { navController.popBackStack() }, url = url)
            }
            animatedComposable(Route.TABS) {
                TabsPage(
                    goBack = {
                        navController.popBackStack()
                        appViewModel.clearTabs()
                    },
                    navToCustomTag = { postItem, tag -> navToCustomTag(postItem, tag) },
                    appViewModel = appViewModel,
                )
            }
            animatedComposable(
                route = "downloads/{path}",
                arguments = listOf(navArgument("path") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "hviewer://downloads/{path}" })
            ) { backStackEntry ->
                val path = backStackEntry.arguments?.getString("path")
                DownloadsPage(
                    goBack = navController::popBackStack,
                    initialDir = path
                )
            }
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