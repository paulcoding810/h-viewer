package com.paulcoding.hviewer.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.helper.makeToast

@Composable
fun H7Tap(modifier: Modifier = Modifier, onDevModeChange: (Boolean) -> Unit) {
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    TextButton(
        onClick = {
            val current = System.currentTimeMillis()
            if (current - lastTapTime > 2000) {
                tapCount = 1
            } else {
                tapCount++
                if (tapCount >= 7) {
                    makeToast("Dev Mode Enabled")
                    onDevModeChange(true)
                    tapCount = 0
                }
            }
            lastTapTime = current
        }, modifier = modifier
            .padding(16.dp)
    ) {
        Text("Version ${BuildConfig.VERSION_NAME}")
    }
}