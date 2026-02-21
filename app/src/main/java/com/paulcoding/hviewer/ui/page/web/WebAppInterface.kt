package com.paulcoding.hviewer.ui.page.web

import android.webkit.JavascriptInterface

class WebAppInterface {
    @JavascriptInterface
    fun senData(data: String) {
        println(data)
    }
}