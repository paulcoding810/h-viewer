package com.paulcoding.hviewer.ui.favorite

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.PostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePage(
    appViewModel: AppViewModel,
    navToImages: (PostItem) -> Unit,
    goBack: () -> Boolean
) {
    val viewModel: AppViewModel = viewModel()
    val favoritePosts by viewModel.favoritePosts.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Favorite") }, navigationIcon = {
                HBackIcon { goBack() }
            })
        }) { paddings ->
        LazyColumn(modifier = Modifier.padding(paddings)) {
            items(items = favoritePosts.reversed(), key = { it.url }) { item ->
                FavoriteItem(item, navToImages = { navToImages(it) }, deleteFavorite = {
                    appViewModel.deleteFavorite(it)
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
    navToImages: (PostItem) -> Unit,
    deleteFavorite: (PostItem) -> Unit
) {
    PostCard(post, isFavorite = true, setFavorite = {
        deleteFavorite(post)
    }) {
        navToImages(post)
    }
}
