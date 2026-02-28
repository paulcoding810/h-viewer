package com.paulcoding.hviewer.ui.page.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.sites.navToPost
import com.paulcoding.hviewer.ui.page.sites.navToCustomTag

fun NavGraphBuilder.historyNavigation(navController: NavController) = composable<Routes.History>() {
    HistoryPage(
        goBack =navController::navigateUp,
        navToImages = navController::navToPost,
        navToCustomTag = navController::navToCustomTag,
    )
}


fun NavController.navToHistory() = navigate(Routes.History)