package com.paulcoding.hviewer.ui.page.web

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HWebView(
    modifier: Modifier = Modifier,
    url: String,
    onWebViewCreated: (WebView) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = HWebViewClient(url)
                val jsInterface = WebAppInterface()
                addJavascriptInterface(jsInterface, "HViewer")
                loadUrl(url)
                onWebViewCreated(this)
            }
        }
    )
}
