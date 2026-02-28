package com.paulcoding.hviewer.ui.page.sites.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import com.paulcoding.hviewer.ui.component.HImage
import com.paulcoding.hviewer.ui.component.HideSystemBar
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun ImageModal(
    url: String,
    modifier: Modifier = Modifier,
    onImageFirstOffset: (Offset) -> Unit = {},
    dismiss: () -> Unit = {}
) {
    val zoomableState = rememberZoomableState(ZoomSpec(maxZoomFactor = 5f))
    var onImageFirstOffsetTriggered = remember { false }

    val doubleClickToZoomListener =
        remember {
            DoubleClickToZoomListener { _, _ ->
                dismiss()
            }
        }

    HideSystemBar(true, showSystemBarOnBack = false) {
        dismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .zoomable(
                state = zoomableState,
                onClick = { dismiss() },
                onDoubleClick = doubleClickToZoomListener
            )
    ) {
        HImage(
            url = url,
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned {
                    if (!onImageFirstOffsetTriggered) {
                        onImageFirstOffset(it.localToWindow(Offset.Zero))
                        onImageFirstOffsetTriggered = true
                    }
                },
        )
    }
}

@Preview
@Composable
private fun PreviewImageModal() {
    ImageModal(url = "https://i.imgur.com/tGbaZCY.png")
}