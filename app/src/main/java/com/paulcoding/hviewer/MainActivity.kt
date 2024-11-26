package com.paulcoding.hviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paulcoding.hviewer.ui.page.AppEntry
import com.paulcoding.hviewer.ui.theme.HViewerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HViewerTheme {
                AppEntry()
            }
        }
    }
}
