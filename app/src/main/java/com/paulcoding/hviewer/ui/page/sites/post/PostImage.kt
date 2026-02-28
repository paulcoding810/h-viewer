package com.paulcoding.hviewer.ui.page.sites.post

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.extensions.openInBrowser
import com.paulcoding.hviewer.ui.component.HImage

@SuppressLint("ContextCastToActivity")
@Composable
fun PostImage(url: String, onDoubleTap: () -> Unit = {}, onTap: () -> Unit = {}) {
    val showMenu = remember { mutableStateOf(false) }
    val menuOffset = remember { mutableStateOf(Pair(0f, 0f)) }
    val context = LocalActivity.current

    Box(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = { offset ->
                showMenu.value = true
                menuOffset.value = Pair(offset.x, offset.y)
            },
            onDoubleTap = { onDoubleTap() },
            onTap = { onTap() }
        )
    }) {
        HImage(
            url = url
        )

        DropdownMenu(
            expanded = showMenu.value,
            onDismissRequest = { showMenu.value = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    showMenu.value = false
                    context?.openInBrowser(url)
                },
                text = {
                    Text(stringResource(R.string.open_in_browser))
                }
            )
        }
    }
}