package com.paulcoding.hviewer.ui.page.search

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.helper.BasePaginationHelper
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.page.AppViewModel
import com.paulcoding.hviewer.ui.page.posts.PostList
import com.paulcoding.hviewer.ui.page.posts.PostsViewModel
import com.paulcoding.hviewer.ui.page.posts.PostsViewModelFactory
import com.paulcoding.hviewer.ui.page.posts.TabsIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    appViewModel: AppViewModel,
    navToImages: (post: PostItem) -> Unit,
    navToCustomTag: (PostItem, Tag) -> Unit,
    navToTabs: () -> Unit,
    goBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }

    val viewModel: PostsViewModel = viewModel(
        factory = PostsViewModelFactory(
            appViewModel.getCurrentSiteConfig(),
            postUrl = "",
            isSearch = true
        ),
    )
    val uiState by viewModel.stateFlow.collectAsState()
    val tabs by appViewModel.tabs.collectAsState(initial = listOf())
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val focusManager = LocalFocusManager.current
    var tabsIconPosition by remember { mutableStateOf(Offset.Zero) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    fun submit() {
        viewModel.setQueryAndSearch(query)
        focusManager.clearFocus()
    }

    LaunchedEffect(query) {
        if (query.isEmpty())
            focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    HBackIcon { goBack() }
                },
                actions = {
                    if (uiState.postItems.isNotEmpty())
                        HPageProgress(uiState.postsPage, uiState.postsTotalPage)
                    TabsIcon(
                        size = tabs.size,
                        onGloballyPositioned = { tabsIconPosition = it },
                        onClick = navToTabs
                    )
                },
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            query,
                            textStyle = TextStyle(fontSize = 14.sp),
                            onValueChange = { query = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(onGo = { submit() }),
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .weight(1f)
                                .height(52.dp),
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
                },
                scrollBehavior = scrollBehavior
            )
        }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {

            SearchPageContent(
                appViewModel = appViewModel,
                endPos = tabsIconPosition,
                navToCustomTag = navToCustomTag,
                viewModel = viewModel
            ) { post ->
                navToImages(post)
            }
        }
    }
}

@Composable
internal fun SearchPageContent(
    appViewModel: AppViewModel,
    viewModel: PostsViewModel,
    endPos: Offset = Offset.Zero,
    navToCustomTag: (PostItem, Tag) -> Unit,
    onClick: (PostItem) -> Unit
) {
    val uiState by viewModel.stateFlow.collectAsState()
    val favoriteSet by appViewModel.favoriteSet.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    val paginationHelper = remember {
        BasePaginationHelper(
            buffer = 5,
            isLoading = { uiState.isLoading },
            hasMore = viewModel::canLoadMorePostsData,
            loadMore = viewModel::getNextPosts
        )
    }


    PostList(
        paginationHelper = paginationHelper,
        favoriteSet = favoriteSet,
        endPos = endPos,
        hidesEmpty = true, // TODO: hide empty on queried & empty
        onAddToTabs = appViewModel::addTab,
        setFavorite = { post, isFavorite ->
            if (isFavorite)
                appViewModel.addFavorite(post)
            else
                appViewModel.deleteFavorite(post)
        },
        navToCustomTag = navToCustomTag,
        isLoading = uiState.isLoading,
        onRefresh = { viewModel.getPosts(1) },
        onClick = onClick,
        listPosts = uiState.postItems,
    )
}

