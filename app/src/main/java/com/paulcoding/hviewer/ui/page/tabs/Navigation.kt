package com.paulcoding.hviewer.ui.page.tabs

import android.annotation.SuppressLint
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.helper.fadeInWithBlur
import com.paulcoding.hviewer.helper.fadeOutWithBlur
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.sites.navToCustomTag

@SuppressLint("UnrememberedGetBackStackEntry")
fun NavGraphBuilder.tabsNavigation(navController: NavController, viewModel: TabsViewModel,) {
    composable<Routes.Tabs>(
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) {

        val parentEntry = remember(navController) {
            navController.getBackStackEntry(Routes.Sites)
        }

        TabsPage(
            viewModel = viewModel,
            viewModelStoreOwner = parentEntry,
            goBack = navController::navigateUp,
            navToCustomTag = navController::navToCustomTag,
        )
    }
}

fun NavController.navToTabs() = navigate(Routes.Tabs)