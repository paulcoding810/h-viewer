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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.extensions.isScrolledToEnd
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HGoTop
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.FavoriteCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    appViewModel: AppViewModel,
    navToImages: (post: PostItem) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    goBack: () -> Unit,
) {
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(appViewModel.getCurrentSiteConfig()),
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
        TopAppBar(title = { Text(stringResource(R.string.search)) }, navigationIcon = {
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
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .weight(1f),
                    trailingIcon = {
                        if (query.isNotEmpty())
                            HIcon(Icons.Outlined.Clear) {
                                query = ""
                                focusRequester.requestFocus()
                            }
                    }
                )
                HIcon(Icons.Outlined.Search) { submit() }
            }
            PageContent(
                appViewModel = appViewModel,
                navToCustomTag = navToCustomTag,
                viewModel = viewModel
            ) { post ->
                navToImages(post)
            }
        }
    }
}

// TODO: duplicated PageContent
@Composable
fun PageContent(
    appViewModel: AppViewModel,
    viewModel: SearchViewModel,
    navToCustomTag: (PostItem, Tag) -> Unit,
    onClick: (PostItem) -> Unit
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.stateFlow.collectAsState()
    val listFavorite by appViewModel.favoritePosts.collectAsState(initial = emptyList())

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostsData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextPosts()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.postItems, key = { it.url }) { post ->
                FavoriteCard(post,
                    isFavorite = listFavorite.find { it.url == post.url } != null,
                    setFavorite = { isFavorite ->
                        if (isFavorite)
                            appViewModel.addFavorite(post)
                        else
                            appViewModel.deleteFavorite(post)
                    },
                    onTagClick = {
                        navToCustomTag(post, it)
                    }
                )
                {
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

