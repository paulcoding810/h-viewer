package com.paulcoding.hviewer.ui.favorite

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.PostCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePage(
    appViewModel: AppViewModel,
    navToImages: (PostItem) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    goBack: () -> Boolean
) {
    val viewModel: AppViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val favoritePosts by viewModel.favoritePosts.collectAsState(initial = emptyList())

    fun onDelete(post: PostItem) {
        appViewModel.deleteFavorite(post)

        scope.launch {
            val result = snackbarHostState.showSnackbar(
                "${post.name} removed from favorite",
                "Undo",
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
            TopAppBar(title = { Text("Favorite") }, navigationIcon = {
                HBackIcon { goBack() }
            })
        }) { paddings ->
        LazyColumn(modifier = Modifier.padding(paddings)) {
            items(items = favoritePosts, key = { it.url }) { item ->
                FavoriteItem(item, navToImages = { navToImages(item) },
                    onTagClick = { tag -> navToCustomTag(item, tag) },
                    deleteFavorite = {
                        onDelete(item)
                    })
            }
            if (favoritePosts.isEmpty())
                item { HEmpty() }
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
    PostCard(
        post, isFavorite = true,
        setFavorite = {
            deleteFavorite()
        },
        onTagClick = { onTagClick(it) },
    ) {
        navToImages()
    }
}
