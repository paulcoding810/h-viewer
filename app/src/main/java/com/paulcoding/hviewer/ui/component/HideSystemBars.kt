package com.paulcoding.hviewer.ui.component

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SystemBar(isHidden: Boolean) {
    val context = LocalContext.current
    val window = (context as? ComponentActivity)?.window
    val view = LocalView.current

    fun hideSystemBars() {
        window?.let {
            val controller = WindowInsetsControllerCompat(it, it.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun showSystemBars() {
        window?.let {
            val controller = WindowInsetsControllerCompat(it, view)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    LaunchedEffect(isHidden) {
        if (isHidden) {
            hideSystemBars()
        } else {
            showSystemBars()
        }
    }
}