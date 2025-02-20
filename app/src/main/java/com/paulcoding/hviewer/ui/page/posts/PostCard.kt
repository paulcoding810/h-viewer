package com.paulcoding.hviewer.ui.page.posts

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.MainActivity
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
    isFavorite: Boolean = false,
    setFavorite: (Boolean) -> Unit = {},
    onTagClick: (Tag) -> Unit = {},
    onAddToTabs: () -> Unit = {},
    onClick: () -> Unit,
) {
    PostCard(
        postItem = postItem,
        onTagClick = onTagClick,
        onAddToTabs = onAddToTabs,
        onClick = onClick
    ) {
        HFavoriteIcon(
            modifier = Modifier.align(Alignment.TopEnd),
            isFavorite = isFavorite
        ) {
            setFavorite(!isFavorite)
        }
    }
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    postItem: PostItem,
    onTagClick: (Tag) -> Unit = {},
    onClick: () -> Unit,
    onAddToTabs: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val context = LocalContext.current as MainActivity

    LaunchedEffect(isBottomSheetVisible) {
        if (isBottomSheetVisible) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        border = CardDefaults.outlinedCardBorder(),
        shape = CardDefaults.outlinedShape,
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300)
                ),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                HImage(
                    url = postItem.thumbnail
                )
                Text(postItem.name, fontSize = 12.sp)
            }
            HIcon(Icons.Outlined.Info) {
                isBottomSheetVisible = true
            }
            HIcon(Icons.Outlined.Tab, modifier = Modifier.align(Alignment.TopCenter)) {
                onAddToTabs()
            }
            content()
        }
    }

//    TODO: Remove AnimatedVisibility
    AnimatedVisibility(isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                isBottomSheetVisible = false
            }
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
                    TextButton(
                        onClick = {
                            isBottomSheetVisible = false
                            context.openInBrowser(url)
                        },
                    ) {
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
                                isBottomSheetVisible = false
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