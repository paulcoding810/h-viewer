package com.paulcoding.hviewer.ui.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.FavoriteCard
import com.paulcoding.hviewer.ui.page.posts.TabsIcon
import com.paulcoding.hviewer.ui.page.tabs.AddToCartAnimation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePage(
    appViewModel: AppViewModel,
    tabs: List<PostItem>,
    navToTabs: () -> Unit,
    navToImages: (PostItem) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    goBack: () -> Boolean
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val favoritePosts by appViewModel.favoritePosts.collectAsState(initial = emptyList())
    var tabsIconPosition by remember { mutableStateOf(Offset.Zero) }

    var startPos by remember { mutableStateOf(Offset.Zero) }
    var isAnimating by remember { mutableStateOf(false) }

    fun onDelete(post: PostItem) {
        appViewModel.deleteFavorite(post)

        scope.launch {
            val result = snackbarHostState.showSnackbar(
                context.getString(R.string.post_removed_from_favorite, post.name),
                context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    appViewModel.addFavorite(post, true)
                }

                SnackbarResult.Dismissed -> {
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.favorite)) }, navigationIcon = {
                HBackIcon { goBack() }
            }, actions = {
                TabsIcon(
                    onClick = navToTabs,
                    size = tabs.size,
                    onGloballyPositioned = { tabsIconPosition = it })
            })
        },
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = favoritePosts, key = { it.url }) { item ->
                    FavoriteCard(
                        item,
                        isFavorite = true,
                        setFavorite = {
                            onDelete(item)
                        },
                        onTagClick = { tag -> navToCustomTag(item, tag) },
                        onAddToTabs = {
                            startPos = it
                            isAnimating = true
                            appViewModel.addTab(item)
                        },
                        onClick = {
                            navToImages(item)
                        })
                }
            }
            if (favoritePosts.isEmpty())
                HEmpty()
        }

        AddToCartAnimation(
            isAnimating = isAnimating,
            startPosition = startPos,
            endPosition = tabsIconPosition.copy(y = tabsIconPosition.y - 100),
            onAnimationEnd = { isAnimating = false },
        )
    }

}