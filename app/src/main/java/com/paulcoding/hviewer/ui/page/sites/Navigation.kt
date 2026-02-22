package com.paulcoding.hviewer.ui.page.sites

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.paulcoding.hviewer.helper.fadeInWithBlur
import com.paulcoding.hviewer.helper.fadeOutWithBlur
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.downloads.navToDownloads
import com.paulcoding.hviewer.ui.page.favorite.navToFavorite
import com.paulcoding.hviewer.ui.page.history.navToHistory
import com.paulcoding.hviewer.ui.page.settings.navToSettings
import com.paulcoding.hviewer.ui.page.sites.post.PostPage
import com.paulcoding.hviewer.ui.page.sites.search.SearchPage
import com.paulcoding.hviewer.ui.page.sites.site.PostsPage
import com.paulcoding.hviewer.ui.page.sites.tag.CustomTagPage
import com.paulcoding.hviewer.ui.page.tabs.navToTabs
import com.paulcoding.hviewer.ui.page.web.navToWebView
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.sitesNavigation(navController: NavController) {
    composable<Routes.Sites>(
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) {
        SitesPage(
            navToSite = navController::navToPosts,
            navToSettings = navController::navToSettings,
            navToFavorite = navController::navToFavorite,
            navToHistory = navController::navToHistory,
            navToDownloads = navController::navToDownloads,
        )
    }

    composable<Routes.Site>(
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) { backStackEntry ->
        val parentEntry = remember(navController) {
            navController.getBackStackEntry(Routes.Sites)
        }
        PostsPage(
            viewModelStoreOwner = parentEntry,
            navToPost = navController::navToPost,
            navToSearch = { navController.navToSearch(backStackEntry.toRoute<Routes.Site>().url) },
            navToCustomTag = navController::navToCustomTag,
            navToTabs = navController::navToTabs,
            goBack = navController::navigateUp
        )
    }

    composable<Routes.CustomTag>(
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) {
        CustomTagPage(
            navToCustomTag = navController::navToCustomTag,
            navToTabs = navController::navToTabs,
            navToImages = navController::navToPost,
            goBack = navController::navigateUp
        )
    }

    composable<Routes.Post>(
        typeMap = Routes.Post.typeMap,
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) { backStackEntry ->
        PostPage(
            viewModel = koinViewModel(key = backStackEntry.toRoute<Routes.Post>().postItem.url),
            navToWebView = navController::navToWebView,
            navToCustomTag = navController::navToCustomTag,
            goBack = navController::navigateUp,
        )
    }

    composable<Routes.Search>(
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) { backStackEntry ->
        SearchPage(
            viewModel = koinViewModel(
                parameters = { parametersOf(backStackEntry.toRoute<Routes.Search>().url, true) }
            ),
            navToImages = navController::navToPost,
            navToCustomTag = navController::navToCustomTag,
            navToTabs = navController::navToTabs,
            goBack = navController::navigateUp,
        )
    }
}

fun NavController.navToSearch(url: String) = navigate(Routes.Search(url))

fun NavController.navToPost(postItem: PostItem) {
    navigate(Routes.Post(postItem))
}

fun NavController.navToPosts(url: String, isSearch: Boolean) = navigate(Routes.Site(url, isSearch))

fun NavController.navToCustomTag(tag: Tag) = navigate(Routes.CustomTag(tag.url, tag.name))
