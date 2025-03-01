package com.paulcoding.hviewer.ui.page.web

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HWebView(
    modifier: Modifier = Modifier,
    url: String,
) {
    class WebAppInterface {
        @JavascriptInterface
        fun senData(data: String) {
            println(data)
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(webview: WebView, url: String?) {
                        super.onPageFinished(webview, url)
                        webview.loadUrl("javascript:window.HViewer.senData('Hello from WebView')")
                    }
                }
                val jsInterface = WebAppInterface()
                addJavascriptInterface(jsInterface, "HViewer")
                loadUrl(url)
            }
        }
    )
}