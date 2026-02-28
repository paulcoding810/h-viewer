package com.paulcoding.hviewer.ui.page.sites.search

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.helper.BasePaginationHelper
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.component.HPageProgress
import com.paulcoding.hviewer.ui.page.sites.composable.PostList
import com.paulcoding.hviewer.ui.page.sites.composable.TabsIcon
import com.paulcoding.hviewer.ui.page.sites.site.PostsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    viewModel: PostsViewModel,
    navToImages: (post: PostItem) -> Unit,
    navToCustomTag: (Tag) -> Unit,
    navToTabs: () -> Unit,
    goBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }

    val uiState by viewModel.stateFlow.collectAsState()
    val postItems by viewModel.postItems.collectAsState(emptyList())
    val tabsCount by viewModel.tabsManager.tabsSizeFlow.collectAsState(0)

    val focusRequester = remember { FocusRequester() }
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
                    if (postItems.isNotEmpty())
                        HPageProgress(uiState.postsPage, uiState.postsTotalPage)
                    TabsIcon(
                        size = tabsCount,
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
                viewModel = viewModel,
                postItems = postItems,
                endPos = tabsIconPosition,
                navToCustomTag = navToCustomTag,
                addTab = viewModel.tabsManager::addTab,
            ) { post ->
                navToImages(post)
            }
        }
    }
}

@Composable
internal fun SearchPageContent(
    viewModel: PostsViewModel,
    postItems: List<PostItem>,
    addTab: (PostItem) -> Unit,
    endPos: Offset = Offset.Zero,
    navToCustomTag: (Tag) -> Unit,
    onClick: (PostItem) -> Unit
) {
    val uiState by viewModel.stateFlow.collectAsState()

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
        endPos = endPos,
        hidesEmpty = true, // TODO: hide empty on queried & empty
        onAddToTabs = addTab,
        setFavorite = { post, isFavorite ->
            viewModel.toggleFavorite(post)
        },
        navToCustomTag = navToCustomTag,
        isLoading = uiState.isLoading,
        onRefresh = { viewModel.getPosts(1) },
        onClick = onClick,
        listPosts = postItems,
    )
}

