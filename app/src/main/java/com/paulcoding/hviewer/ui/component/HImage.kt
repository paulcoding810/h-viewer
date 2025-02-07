package com.paulcoding.hviewer.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.SubcomposeAsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun HImage(url: String, modifier: Modifier = Modifier) {
    val headers = NetworkHeaders.Builder()
        .set(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        )
        .build()
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .httpHeaders(headers)
            .crossfade(200)
            .build(),
        contentDescription = url,
        loading = { HLoading() },
        error = { HEmpty() },
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
    )
}

