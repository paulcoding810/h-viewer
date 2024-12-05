package com.paulcoding.hviewer.ui.page.search

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HGoTop
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.icon.EditIcon
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.PostCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    appViewModel: AppViewModel,
    navToImages: (post: PostItem) -> Unit,
    goBack: () -> Unit,
) {
    val appState by appViewModel.stateFlow.collectAsState()
    val siteConfig = appState.site.second

    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(siteConfig),
    )
    val uiState by viewModel.stateFlow.collectAsState()
    var query by remember { mutableStateOf(viewModel.stateFlow.value.query) }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun submit() {
        viewModel.setQueryAndSearch(query)
        focusManager.clearFocus()
    }

    LaunchedEffect(query) {
        if (query.isEmpty())
            focusRequester.requestFocus()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Search") }, navigationIcon = {
            HBackIcon { goBack() }
        },
            actions = {
                if (uiState.postItems.isNotEmpty())
                    HPageProgress(uiState.postsPage, uiState.postsTotalPage)
            })
    }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    query,
                    onValueChange = { query = it },
                    label = { Text("Search") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = { submit() }),
                    modifier = Modifier.focusRequester(focusRequester)
                )
                IconButton(onClick = {
                    submit()
                }) {
                    Icon(EditIcon, contentDescription = "Search")
                }
            }
            PageContent(appViewModel = appViewModel, viewModel = viewModel) { post ->
                navToImages(post)
            }
        }
    }
}


@Composable
fun PageContent(
    appViewModel: AppViewModel,
    viewModel: SearchViewModel,
    onClick: (PostItem) -> Unit
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.stateFlow.collectAsState()
    val listFavorite by appViewModel.favoritePosts.collectAsState(initial = emptyList())

    uiState.error?.let {
        Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostsData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextPosts()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState
        ) {
            items(uiState.postItems) { post ->
                PostCard(post,
                    isFavorite = listFavorite.find { it.url == post.url } != null,
                    setFavorite = { isFavorite ->
                        if (isFavorite)
                            appViewModel.addFavorite(post)
                        else
                            appViewModel.deleteFavorite(post)
                    }
                ) {
                    onClick(post)
                }
            }
            if (uiState.isLoading)
                item {
                    HLoading()
                }
            else if (uiState.postItems.isEmpty() && uiState.query.isNotEmpty())
                item {
                    HEmpty(
                        title = "No posts found",
                    )
                }
        }
        HGoTop(listState)
    }
}

