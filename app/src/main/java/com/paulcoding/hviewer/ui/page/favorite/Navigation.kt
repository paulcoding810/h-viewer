package com.paulcoding.hviewer.ui.page.favorite

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.ui.page.Routes
import com.paulcoding.hviewer.ui.page.sites.navToPost
import com.paulcoding.hviewer.ui.page.sites.navToCustomTag
import com.paulcoding.hviewer.ui.page.tabs.navToTabs

fun NavGraphBuilder.favoriteNavigation(navController: NavController) = composable<Routes.Favorite>() {
    FavoritePage(
        navToImages = navController::navToPost,
        navToTabs = navController::navToTabs,
        navToCustomTag = navController::navToCustomTag,
        goBack = navController::navigateUp
    )
}

fun NavController.navToFavorite() = navigate(Routes.Favorite)