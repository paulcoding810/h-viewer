package com.paulcoding.hviewer.ui.page.webeditor

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.helper.alsoLog
import com.paulcoding.hviewer.helper.log
import com.paulcoding.hviewer.helper.readFile
import com.paulcoding.hviewer.helper.writeFile
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.component.HBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebEditorPage(goBack: () -> Unit, siteConfig: SiteConfig) {
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = { Text("Editor") }, navigationIcon = {
            HBackIcon { goBack() }
        })
    }) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            WebViewWithJavaScriptInterface(
                script = context.readFile(siteConfig.scriptFile).alsoLog(siteConfig.scriptFile),
                onScriptChange = {
                    println(it)
                    context.writeFile(it, siteConfig.scriptFile)
//                    goBack()
                })
        }
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewWithJavaScriptInterface(
    modifier: Modifier = Modifier,
    script: String,
    onScriptChange: (script: String) -> Unit
) {

    fun sendDataToWebView(webView: WebView, data: String) {
        val jsCode = """javascript:fillData(`$data`);"""
        webView.evaluateJavascript(jsCode, null)
    }

    class WebAppInterface() {
        @JavascriptInterface
        fun sendDataToApp(data: String) {
            onScriptChange(data)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                // Enable JavaScript
                settings.javaScriptEnabled = true

                // Set the WebViewClient to avoid opening URLs in external browsers
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        sendDataToWebView(view, script)
                    }
                }

                // Add the JavaScript interface
                val jsInterface = WebAppInterface()

                addJavascriptInterface(jsInterface, "Android")
                loadUrl("file:///android_asset/www/index.html")
            }
        }
    )
}

