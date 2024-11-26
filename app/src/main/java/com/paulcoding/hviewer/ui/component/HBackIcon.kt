package com.paulcoding.hviewer.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun HBackIcon(onClick: () -> Unit) {
    IconButton(onClick = { onClick() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
    }
}