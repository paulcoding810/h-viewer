package com.paulcoding.hviewer.ui.page.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.PostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    goBack: () -> Unit, appViewModel: AppViewModel,
    navToImages: (PostItem) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    deleteHistory: (post: PostItem) -> Unit
) {
    val historyPosts by appViewModel.historyPosts.collectAsState(initial = listOf())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
            )
        }
    ) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(historyPosts) {
                    PostCard(
                        postItem = it,
                        onTagClick = { tag ->
                            navToCustomTag(it, tag)
                        },
                        onClick = {
                            navToImages(it)
                        }) {
                        HIcon(
                            Icons.Outlined.Delete,
                            "Delete",
                        ) {
                            deleteHistory(it)
                        }
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