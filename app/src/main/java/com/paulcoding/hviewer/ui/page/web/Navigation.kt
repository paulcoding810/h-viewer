package com.paulcoding.hviewer.ui.page.web

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.ui.page.Routes

fun NavGraphBuilder.webNavigation(navController: NavController) = composable<Routes.WebView>() { backStackEntry ->
    val url = backStackEntry.arguments?.getString("url") ?: ""
    WebPage(goBack = { navController.popBackStack() }, url)
}

fun NavController.navToWebView(url: String) = navigate(Routes.WebView(url))