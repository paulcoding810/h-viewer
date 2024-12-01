package com.paulcoding.hviewer.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.paulcoding.hviewer.ui.icon.SettingsIcon

@Composable
fun HBackIcon(onClick: () -> Unit) {
    HIcon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack) {
        onClick()
    }
}

@Composable
fun HIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = SettingsIcon,
    description: String = "",
    onClick: () -> Unit
) {
    IconButton(onClick = { onClick() }, modifier = modifier) {
        Icon(imageVector, description)
    }
}