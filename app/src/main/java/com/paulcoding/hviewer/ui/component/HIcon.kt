package com.paulcoding.hviewer.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.ui.theme.favorite

@Composable
fun HBackIcon(onClick: () -> Unit) {
    HIcon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack) {
        onClick()
    }
}

@Composable
fun HFavoriteIcon(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    rounded: Boolean = false,
    onClick: () -> Unit
) {
    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder

    val colors =
        if (isFavorite) IconButtonDefaults.filledTonalIconButtonColors(contentColor = MaterialTheme.colorScheme.favorite) else IconButtonDefaults.filledTonalIconButtonColors()

    HIcon(
        modifier = modifier,
        imageVector = icon,
        onClick = onClick,
        colors = colors,
        rounded = rounded
    )
}

@Composable
fun HIcon(
    imageVector: ImageVector = Icons.Outlined.Settings,
    description: String = "",
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    rounded: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    if (rounded)
        FilledTonalIconButton(
            onClick = { onClick() },
            enabled = enabled,
            colors = colors
        ) {
            Icon(
                imageVector, description,
                modifier = modifier,
            )
        }
    else
        IconButton(
            onClick = { onClick() },
            enabled = enabled,
            colors = colors.copy(containerColor = Color.Transparent)
        ) {
            Icon(
                imageVector, description,
                modifier = modifier,
            )
        }
}


@Preview
@Composable
private fun PreviewHFavoriteIcon() {
    HFavoriteIcon(isFavorite = true) {}
}

@Preview
@Composable
private fun PreviewHNotFavoriteIcon() {
    HFavoriteIcon(isFavorite = false, rounded = true) {}
}

@Preview
@Composable
private fun PreviewHIcon() {
    Column {
        HIcon(onClick = {})
        HIcon(modifier = Modifier.size(128.dp), imageVector = Icons.Outlined.Settings, onClick = {})
    }
}