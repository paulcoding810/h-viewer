package com.paulcoding.hviewer.ui.page.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.Tag
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.sites.composable.FavoriteCard
import com.paulcoding.hviewer.ui.page.sites.composable.TabsIcon
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritePage(
    viewModel: FavoriteViewModel = koinViewModel(),
    navToTabs: () -> Unit,
    navToImages: (PostItem) -> Unit,
    navToCustomTag: (Tag) -> Unit,
    goBack: () -> Boolean
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val favoritePosts by viewModel.favoritePostsWithQuery.collectAsState()
    val query by viewModel.query.collectAsState()
    val tabsCount by viewModel.tabsManager.tabsSizeFlow.collectAsState(initial = 0)

    var tabsIconPosition by remember { mutableStateOf(Offset.Zero) }
    var startPos by remember { mutableStateOf(Offset.Zero) }
    var isAnimating by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    fun removeWithUndo(post: PostItem) {
        viewModel.onAction(FavoriteViewModel.Action.Delete(post))

        scope.launch {
            val result = snackbarHostState.showSnackbar(
                context.getString(R.string.post_removed_from_favorite, post.name),
                context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    viewModel.onAction(FavoriteViewModel.Action.UndoDelete)
                }

                SnackbarResult.Dismissed -> {
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            FavoriteTopBar(
                scrollBehavior = scrollBehavior,
                query = query,
                onAction = viewModel::onAction,
                onTabIconGloballyPositioned = { tabsIconPosition = it },
                navToTabs = navToTabs,
                tabsCount = tabsCount,
                goBack = goBack
            )
        },
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = favoritePosts, key = { it.url }) { item ->
                    FavoriteCard(
                        item,
                        isFavorite = true,
                        setFavorite = {
                            removeWithUndo(item)
                        },
                        onTagClick = navToCustomTag,
                        onAddToTabs = {
                            startPos = it
                            isAnimating = true
                            viewModel.tabsManager.addTab(item)
                        },
                        onClick = {
                            navToImages(item)
                        })
                }
            }
            if (favoritePosts.isEmpty()) {
                HEmpty()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    query: String,
    onAction: (FavoriteViewModel.Action) -> Unit,
    onTabIconGloballyPositioned: (Offset) -> Unit,
    navToTabs: () -> Unit,
    tabsCount: Int,
    goBack: () -> Boolean
) {
    var showSearchBar by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LargeTopAppBar(
        title = {
            if (!showSearchBar) {
                Text(text = stringResource(R.string.favorite))
            } else {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                OutlinedTextField(
                    value = query,
                    modifier = Modifier.focusRequester(focusRequester),
                    textStyle = TextStyle(fontSize = 14.sp),
                    onValueChange = { onAction(FavoriteViewModel.Action.QueryChanged(it)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() },
                    ),
                    trailingIcon = {
                        if (query.isNotEmpty())
                            HIcon(Icons.Outlined.Clear) {
                                onAction(FavoriteViewModel.Action.QueryChanged(""))
                            }
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            HBackIcon { goBack() }
        }, actions = {
            if (showSearchBar) {
                HIcon(Icons.Default.Close, onClick = {
                    showSearchBar = false
                    onAction(FavoriteViewModel.Action.QueryChanged(""))
                })
            } else {
                HIcon(Icons.Default.Search, onClick = {
                    showSearchBar = true
                })
            }
            TabsIcon(
                onClick = navToTabs,
                size = tabsCount,
                onGloballyPositioned = onTabIconGloballyPositioned
            )
        }
    )
}