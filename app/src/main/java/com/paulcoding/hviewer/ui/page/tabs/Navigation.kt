package com.paulcoding.hviewer.ui.page.tabs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.helper.fadeInWithBlur
import com.paulcoding.hviewer.helper.fadeOutWithBlur
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.sites.navToCustomTag

fun NavGraphBuilder.tabsNavigation(navController: NavController, viewModel: TabsViewModel,) {
    composable<Routes.Tabs>(
        enterTransition = { fadeInWithBlur() },
        exitTransition = { fadeOutWithBlur() },
        popEnterTransition = { fadeInWithBlur() },
        popExitTransition = { fadeOutWithBlur() }) {
        TabsPage(
            viewModel = viewModel,
            goBack = navController::navigateUp,
            navToCustomTag = navController::navToCustomTag,
        )
    }
}

fun NavController.navToTabs() = navigate(Routes.Tabs)