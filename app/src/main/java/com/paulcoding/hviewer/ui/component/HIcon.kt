package com.paulcoding.hviewer.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun HBackIcon(onClick: () -> Unit) {
    HIcon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack) {
        onClick()
    }
}

@Composable
fun HFavoriteIcon(modifier: Modifier = Modifier, isFavorite: Boolean, onClick: () -> Unit) {
    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    val tint: Color = if (isFavorite) Color.Red else Color.Gray
    HIcon(modifier = modifier, imageVector = icon, tint = tint, onClick = onClick)
}

@Composable
fun HIcon(
    imageVector: ImageVector = Icons.Outlined.Settings,
    description: String = "",
    size: Int = 24,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit
) {
    IconButton(onClick = { onClick() }, modifier = modifier) {
        Icon(imageVector, description, tint = tint, modifier = Modifier.size(size.dp))
    }
}