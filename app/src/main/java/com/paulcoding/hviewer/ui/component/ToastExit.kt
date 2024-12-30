package com.paulcoding.hviewer.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.paulcoding.hviewer.MainActivity
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.makeToast
import java.util.concurrent.TimeUnit

@Composable
fun ToastExit() {
    val context = LocalContext.current as MainActivity
    var showToast by remember { mutableStateOf(false) }
    var lastBackPressedTime by remember { mutableLongStateOf(0L) }
    BackHandler {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBackPressedTime < TimeUnit.SECONDS.toMillis(3)) {
            context.finish()
        } else {
            showToast = true
            lastBackPressedTime = currentTime
        }
    }

    if (showToast) {
        makeToast(R.string.press_again_to_exit)
        showToast = false
    }
}