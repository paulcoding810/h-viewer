package com.paulcoding.hviewer.ui.page.editor

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.paulcoding.hviewer.model.ListScriptType
import com.paulcoding.hviewer.ui.page.Routes

fun NavGraphBuilder.listScriptNavigation(navController: NavController) = composable<Routes.ListScript>() {
    ListScriptPage(
        goBack = navController::navigateUp,
        navToEditor = navController::navToEditor
    )
}

fun NavController.navToListScript(type: ListScriptType) = navigate(Routes.ListScript(type))


fun NavGraphBuilder.editorNavigation(navController: NavController) = composable<Routes.Editor> {
    EditorPage(
        goBack = navController::navigateUp
    )
}

fun NavController.navToEditor(
    type: ListScriptType,
    fileName: String
) = navigate(Routes.Editor(type, fileName))