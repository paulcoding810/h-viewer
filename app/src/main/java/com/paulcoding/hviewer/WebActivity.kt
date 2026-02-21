package com.paulcoding.hviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paulcoding.hviewer.ui.page.web.WebPage

class WebActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            WebPage(
                goBack = ::finish,
                url = intent.data.toString()
            )
        }
    }
}


