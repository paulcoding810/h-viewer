package com.paulcoding.hviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.paulcoding.hviewer.ui.page.AppEntry
import com.paulcoding.hviewer.ui.theme.HViewerTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModal: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModal = ViewModelProvider.create(this).get()

        enableEdgeToEdge()
        setContent {
            HViewerTheme {
                AppEntry(viewModal = viewModal)
            }
        }
    }
}
