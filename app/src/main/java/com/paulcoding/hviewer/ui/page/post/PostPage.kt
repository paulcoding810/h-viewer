package com.paulcoding.hviewer.ui.page.post

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.ui.component.HGoTop
import com.paulcoding.hviewer.ui.component.HImage
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HideSystemBars
import com.paulcoding.hviewer.ui.page.AppViewModel
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun PostPage(appViewModel: AppViewModel, goBack: () -> Unit) {
    val appState by appViewModel.stateFlow.collectAsState()
    val post = appState.post
    val siteConfig = appState.site.second

    val viewModel: PostViewModel = viewModel(
        factory = PostViewModelFactory(post.url, siteConfig = siteConfig)
    )

    val uiState by viewModel.stateFlow.collectAsState()
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    uiState.error?.let {
        Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        viewModel.getImages()
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextImages()
        }
    }

    HideSystemBars()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.images) { image ->
                HImage(
                    modifier = Modifier.clickable { selectedImage = image },
                    url = image
                )
            }
            if (uiState.isLoading)
                item {
                    HLoading()
                }

        }

        HGoTop(listState)

        if (selectedImage != null) {
            ImageModal(url = selectedImage!!) {
                selectedImage = null
            }
        }
    }
}

@Composable
fun ImageModal(url: String, dismiss: () -> Unit) {
    val zoomableState = rememberZoomableState(ZoomSpec(maxZoomFactor = 5f))

    val doubleClickToZoomListener =
        DoubleClickToZoomListener { _, _ ->
            dismiss()
        }

    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(
                        state = zoomableState,
                        onClick = { makeToast("Double click to dismiss") },
                        onDoubleClick = doubleClickToZoomListener
                    )
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = url,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}