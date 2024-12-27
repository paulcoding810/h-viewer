package com.paulcoding.hviewer.ui.page.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulcoding.hviewer.ui.component.HBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPage(goBack: () -> Unit, url: String) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    HBackIcon { goBack() }
                },
            )
        }
    ) { paddings ->
        HWebView(modifier = Modifier.padding(paddings), url = url)
    }
}

