package com.paulcoding.hviewer.ui.page.web

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.paulcoding.hviewer.extensions.copyText
import com.paulcoding.hviewer.extensions.shareText
import com.paulcoding.hviewer.ui.component.HBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPage(goBack: () -> Unit, url: String) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var webViewState by remember { mutableStateOf<android.webkit.WebView?>(null) }

    webViewState?.let {
        val cookie = android.webkit.CookieManager.getInstance().getCookie(webViewState?.url ?: url)
        val userAgent = webViewState?.settings?.userAgentString
    }

    val activity = LocalActivity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    HBackIcon { goBack() }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            Icons.Default.MoreHoriz,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) { paddings ->
        HWebView(
            modifier = Modifier.padding(paddings), 
            url = url,
            onWebViewCreated = { webViewState = it }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column() {
                ListItem(
                    modifier = Modifier.clickable {
                        activity?.copyText(url)
                        showBottomSheet = false
                    },
                    headlineContent = { Text("Copy URL") },
                    leadingContent = { Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL") },
                )
                ListItem(
                    modifier = Modifier.clickable {
                        activity?.shareText(url)
                        showBottomSheet = false
                    },
                    headlineContent = { Text("Share URL") },
                    leadingContent = { Icon(Icons.Default.Share, contentDescription = "Share URL") }
                )
            }
        }
    }
}

