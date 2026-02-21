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
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.ConfirmDialog
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.posts.PostCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPage(
    viewModel: HistoryViewModel = koinViewModel(),
    goBack: () -> Unit,
    navToImages: (PostItem) -> Unit,
    navToCustomTag: (Tag) -> Unit,
) {
    val historyPosts by viewModel.viewedPosts.collectAsState()

    var showsConfirmClearHistory by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) },
                navigationIcon = {
                    HBackIcon { goBack() }
                },
                actions = {
                    HIcon(
                        Icons.Outlined.DeleteForever,
                        "Delete",
                        tint = MaterialTheme.colorScheme.error
                    ) {
                        showsConfirmClearHistory = true
                    }
                }
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
                        onTagClick = navToCustomTag,
                        onClick = {
                            navToImages(it)
                        }) {
                        HIcon(
                            Icons.Outlined.Delete,
                            "Delete",
                        ) {
                            viewModel.deleteHistory(it)
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

        if (showsConfirmClearHistory) {
            ConfirmDialog(
                onDismiss = { showsConfirmClearHistory = false },
                title = stringResource(R.string.clear_history),
                text = stringResource(R.string.clear_history_confirm),
                onConfirm = {
                    viewModel.deleteAllHistory()
                    showsConfirmClearHistory = true
                }
            )
        }
    }
}