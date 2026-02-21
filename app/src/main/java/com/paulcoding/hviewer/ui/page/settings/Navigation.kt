package com.paulcoding.hviewer.ui.page.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.helper.fadeInWithBlur
import com.paulcoding.hviewer.helper.fadeOutWithBlur
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.editor.navToListScript

fun NavGraphBuilder.settingsNavigation(
    navController: NavController,
) = composable<Routes.Settings>(
    enterTransition = { fadeInWithBlur() },
    exitTransition = { fadeOutWithBlur() },
    popEnterTransition = { fadeInWithBlur() },
    popExitTransition = { fadeOutWithBlur() }) {
    SettingsPage(
        navToListScript = navController::navToListScript,
        goBack = navController::navigateUp,
    )
}

fun NavController.navToSettings() = navigate(Routes.Settings)