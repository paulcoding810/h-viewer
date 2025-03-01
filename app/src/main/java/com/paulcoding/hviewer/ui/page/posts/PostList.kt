package com.paulcoding.hviewer.ui.page.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HGoTop
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.page.tabs.AddToCartAnimation

@Composable
fun PostList(
    listPosts: List<PostItem>,
    listFavorite: List<PostItem>,
    endPos: Offset,
    isLoading: Boolean = false,
    hidesEmpty: Boolean = false,
    navToCustomTag: (PostItem, Tag) -> Unit,
    onLoadMore: () -> Unit,
    onAddToTabs: (PostItem) -> Unit,
    onRefresh: () -> Unit = {},
    setFavorite: (PostItem, Boolean) -> Unit,
    onClick: (PostItem) -> Unit
) {

    val listState = rememberLazyListState()

    var startPos by remember { mutableStateOf(Offset.Zero) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.isScrolledToEnd()) {
            onLoadMore()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listPosts, key = { it.url }) { post ->
                FavoriteCard(
                    postItem = post,
                    isFavorite = listFavorite.find { it.url == post.url } != null,
                    onTagClick = { tag ->
                        navToCustomTag(post, tag)
                    },
                    onAddToTabs = {
                        startPos = it
                        isAnimating = true
                        onAddToTabs(post)
                    },
                    setFavorite = { isFavorite ->
                        setFavorite(post, isFavorite)
                    }
                ) {
                    onClick(post)
                }
            }
            if (isLoading)
                item {
                    HLoading()
                }
            else if (listPosts.isEmpty())
                item {
                    if (hidesEmpty) HEmpty(
                        title = "No posts found",
                        message = "Refresh?"
                    ) {
                        onRefresh()
                    }
                }
        }
        HGoTop(listState)


        AddToCartAnimation(
            isAnimating = isAnimating,
            startPosition = startPos,
            endPosition = endPos.copy(y = endPos.y - 100),
            onAnimationEnd = { isAnimating = false },
        )
    }
}
