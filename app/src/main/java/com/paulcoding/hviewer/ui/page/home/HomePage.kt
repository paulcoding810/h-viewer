package com.paulcoding.hviewer.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulcoding.hviewer.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: MainViewModel) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Home") })
    }) {
        Column(modifier = Modifier.padding(it)) { }
    }
}
