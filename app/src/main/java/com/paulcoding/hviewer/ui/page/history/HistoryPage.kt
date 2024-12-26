package com.paulcoding.hviewer.ui.page.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.FavoriteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    goBack: () -> Unit, appViewModel: AppViewModel,
    navToImages: (PostItem) -> Unit
) {
    val historyPosts by appViewModel.historyPosts.collectAsState(initial = listOf())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
            )
        }
    ) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
            ) {
                items(historyPosts) {
                    FavoriteCard(postItem = it.toPostItem()) {
                        navToImages(it.toPostItem())
                    }
                }
            }
            if (historyPosts.isEmpty())
                HEmpty(
                    title = "Wow",
                    message = "Such empty"
                )
        }
    }
}

