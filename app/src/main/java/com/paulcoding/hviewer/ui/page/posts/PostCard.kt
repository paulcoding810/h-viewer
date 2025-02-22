package com.paulcoding.hviewer.ui.page.posts

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.extensions.openInBrowser
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HFavoriteIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HImage

@Composable
fun FavoriteCard(
    postItem: PostItem,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    setFavorite: (Boolean) -> Unit = {},
    onTagClick: (Tag) -> Unit = {},
    onAddToTabs: ((Offset) -> Unit)? = null,
    onClick: () -> Unit,
) {
    PostCard(
        modifier = modifier,
        postItem = postItem,
        onTagClick = onTagClick,
        onAddToTabs = onAddToTabs,
        onClick = onClick
    ) {
        HFavoriteIcon(
            isFavorite = isFavorite
        ) {
            setFavorite(!isFavorite)
        }
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    postItem: PostItem,
    onTagClick: (Tag) -> Unit = {},
    onClick: () -> Unit,
    onAddToTabs: ((Offset) -> Unit)? = null,
    content: @Composable RowScope.() -> Unit = {},
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    var startPos by remember { mutableStateOf(Offset.Zero) }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder(),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300)
                ),
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                HImage(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    url = postItem.thumbnail
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    postItem.name,
                    fontSize = 12.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HIcon(Icons.Outlined.Info) {
                        isBottomSheetVisible = true
                    }
                    content()
                    if (onAddToTabs != null) HIcon(
                        Icons.AutoMirrored.Outlined.OpenInNew,
                        modifier = Modifier
                            .onGloballyPositioned { startPos = it.positionInRoot() },
                    ) {
                        onAddToTabs(startPos)
                    }
                }
            }
        }
    }

    InfoBottomSheet(
        visible = isBottomSheetVisible,
        postItem = postItem,
        onDismissRequest = { isBottomSheetVisible = false },
        onTagClick = {
            onTagClick(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    visible: Boolean,
    postItem: PostItem,
    onDismissRequest: () -> Unit,
    onTagClick: (Tag) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val activity = LocalActivity.current

    LaunchedEffect(visible) {
        if (visible) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    AnimatedVisibility(visible) {
        ModalBottomSheet(
            sheetState = bottomSheetState, onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                postItem.run {
                    SelectionContainer {
                        Text(text = name, fontSize = 20.sp)
                    }
                    TextButton(onClick = {
                        onDismissRequest()
                        activity?.openInBrowser(url)
                    }) {
                        Text(
                            text = url,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 12.sp,
                            color = Color.Blue
                        )
                    }
                    if (size != null) {
                        Text(text = stringResource(R.string.post_size, size))
                    }
                    if (views != null) {
                        Text(text = stringResource(R.string.post_views, views))
                    }
                    postItem.tags?.run {
                        forEach { tag ->
                            TextButton(onClick = {
                                onDismissRequest()
                                onTagClick(tag)
                            }) {
                                Text(text = tag.name)
                            }
                        }
                    }
                }
            }
        }
    }
}
