package com.paulcoding.hviewer.ui.page.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.component.HImage
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun ImageModal(url: String, dismiss: () -> Unit) {
    val zoomableState = rememberZoomableState(ZoomSpec(maxZoomFactor = 5f))

    val doubleClickToZoomListener =
        remember {
            DoubleClickToZoomListener { _, _ ->
                dismiss()
            }
        }

    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .zoomable(
                        state = zoomableState,
                        onClick = {
                            if (!Preferences.showedTutHideImageModal) {
                                makeToast(R.string.double_click_to_dismiss)
                                Preferences.showedTutHideImageModal = true
                            }
                        },
                        onDoubleClick = doubleClickToZoomListener
                    )
            ) {
                HImage(
                    url,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}