package com.paulcoding.hviewer.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.paulcoding.hviewer.MainActivity

@Composable
fun HideSystemBars() {
    val context = LocalContext.current
    val window = (context as? MainActivity)?.window!!
    val controller = WindowInsetsControllerCompat(window, window.decorView)


    DisposableEffect(Unit) {
        window.let {
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }
}