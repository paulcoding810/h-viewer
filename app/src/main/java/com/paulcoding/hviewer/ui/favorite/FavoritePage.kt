package com.paulcoding.hviewer.ui.favorite

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.FavoriteCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePage(
    appViewModel: AppViewModel,
    navToImages: (PostItem) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    goBack: () -> Boolean
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val favoritePosts by appViewModel.favoritePosts.collectAsState(initial = emptyList())

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
            })
        }) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(items = favoritePosts, key = { it.url }) { item ->
                    FavoriteItem(item, navToImages = { navToImages(item) },
                        onTagClick = { tag -> navToCustomTag(item, tag) },
                        deleteFavorite = {
                            onDelete(item)
                        })
                }
            }
            if (favoritePosts.isEmpty())
                HEmpty()
        }
    }

}

@Composable
fun FavoriteItem(
    post: PostItem,
    navToImages: () -> Unit,
    onTagClick: (Tag) -> Unit,
    deleteFavorite: () -> Unit
) {
    FavoriteCard(
        post, isFavorite = true,
        setFavorite = {
            deleteFavorite()
        },
        onTagClick = { onTagClick(it) },
    ) {
        navToImages()
    }
}
