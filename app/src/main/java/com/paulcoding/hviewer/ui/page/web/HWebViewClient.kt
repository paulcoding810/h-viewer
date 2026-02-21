package com.paulcoding.hviewer.ui.page.web

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.paulcoding.hviewer.helper.GlobalData
import com.paulcoding.hviewer.helper.host
import java.io.ByteArrayInputStream

class HWebViewClient(url: String) : WebViewClient() {
    val siteConfig = GlobalData.siteConfigMap[url.host]

    // Pre-compiled set of ad hosts for efficient lookup
    private var AD_HOSTS = setOf(
        "doubleclick.net",
        "googlesyndication.com",
        "adservice.google.com",
        "admob.com",
        "adsystem.com",
    ) + (siteConfig?.ads ?: emptySet())

    companion object {
        // Cached empty response for blocked requests
        private val EMPTY_RESPONSE = WebResourceResponse(
            "text/plain",
            "utf-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    override fun onPageFinished(webview: WebView, url: String?) {
        super.onPageFinished(webview, url)
        webview.loadUrl("javascript:window.HViewer.senData('Hello from WebView')")
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        request?.url?.let { uri ->
            // Check if host is in ad blocklist
            uri.host?.let { host ->
                if (host in AD_HOSTS) {
                    return EMPTY_RESPONSE
                }
            }
        }

        return super.shouldInterceptRequest(view, request)
    }
}