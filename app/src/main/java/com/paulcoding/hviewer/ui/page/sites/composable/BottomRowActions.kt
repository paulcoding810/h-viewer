package com.paulcoding.hviewer.ui.page.sites.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon

@Composable
internal fun BottomRowActions(
    postItem: PostItem,
    onClickInfo: (() -> Unit)? = {},
    onClickFavorite: ((PostItem) -> Unit)? = {},
    onClickRemove: (() -> Unit)? = null,
    onClickRemoveAll: (() -> Unit)? = null,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        onClickInfo?.let {
            HIcon(
                Icons.Outlined.Info,
                size = 32,
                rounded = true
            ) {
                onClickInfo()
            }
        }

        onClickFavorite?.let {
            HFavoriteIcon(isFavorite = postItem.favorite, rounded = true) {
                onClickFavorite(postItem)
            }
        }

        onClickRemove?.let {
            HIcon(
                Icons.Outlined.Close,
                size = 32,
                rounded = true,
                onClick = onClickRemove
            )
        }

        onClickRemoveAll?.let {
            HIcon(
                Icons.Outlined.ClearAll,
                size = 32,
                tint = MaterialTheme.colorScheme.errorContainer,
                rounded = true,
                onClick = onClickRemoveAll
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBottomRowActions() {
    BottomRowActions(
        postItem = PostItem(favorite = true),
        onClickRemove = {},
        onClickRemoveAll = {},
        onClickFavorite = {},
        onClickInfo = {}
    )
}