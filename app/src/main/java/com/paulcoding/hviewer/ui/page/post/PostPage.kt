package com.paulcoding.hviewer.ui.page.post

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainActivity
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.extensions.isScrollingUp
import com.paulcoding.hviewer.extensions.openInBrowser
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HGoTop
import com.paulcoding.hviewer.ui.component.HImage
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HideSystemBars
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.fadeInWithBlur
import com.paulcoding.hviewer.ui.page.fadeOutWithBlur
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun PostPage(appViewModel: AppViewModel, navToWebView: (String) -> Unit, goBack: () -> Unit) {
    val appState by appViewModel.stateFlow.collectAsState()
    val post = appState.post
    val siteConfig = appState.site.second

    val viewModel: PostViewModel = viewModel(
        factory = PostViewModelFactory(post.url, siteConfig = siteConfig)
    )

    val uiState by viewModel.stateFlow.collectAsState()
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
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
            items(uiState.images, key = { it }) { image ->
                PostImage(url = image) {
                    selectedImage = image
                }
            }
            if (uiState.isLoading)
                item {
                    Box(
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        HLoading()
                    }
                }
        }

        HGoTop(listState)

        AnimatedVisibility(
            listState.isScrollingUp().value,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding()
                .statusBarsPadding(),
            enter = fadeInWithBlur(),
            exit = fadeOutWithBlur(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HBackIcon { goBack() }
                Text("${uiState.postPage}/${uiState.postTotalPage}")
            }
        }

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
                    .zoomable(
                        state = zoomableState,
                        onClick = { makeToast(R.string.double_click_to_dismiss) },
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

@Composable
fun PostImage(url: String, onTap: () -> Unit = {}) {
    val showMenu = remember { mutableStateOf(false) }
    val menuOffset = remember { mutableStateOf(Pair(0f, 0f)) }
    val context = LocalContext.current as MainActivity

    Box(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = { offset ->
                println("pressed $url")
                showMenu.value = true
                menuOffset.value = Pair(offset.x, offset.y)
            },
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
                    context.openInBrowser(url)
                },
                text = {
                    Text(stringResource(R.string.open_in_browser))
                }
            )
        }
    }
}