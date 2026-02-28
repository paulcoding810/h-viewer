package com.paulcoding.hviewer.ui.page.downloads

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.ui.page.Routes

fun NavGraphBuilder.downloadsNavigation(navController: NavController) = composable< Routes.Downloads>(){
    DownloadsPage(
        goBack = navController::popBackStack,
        initialDir = null
    )
}

fun NavController.navToDownloads()  = navigate(Routes.Downloads)