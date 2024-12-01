package com.paulcoding.hviewer.ui.page.search

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HLoading
import com.paulcoding.hviewer.ui.icon.EditIcon
import com.paulcoding.hviewer.ui.page.posts.PostItemView


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    navToImages: (postUrl: String) -> Unit,
    siteConfig: SiteConfig,
    goBack: () -> Unit
) {
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(siteConfig),
    )
    var query by remember { mutableStateOf(viewModel.stateFlow.value.query) }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun submit() {
        viewModel.setQuery(query)
        focusManager.clearFocus()
    }

    LaunchedEffect(query) {
        if (query.isEmpty())
            focusRequester.requestFocus()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Search") }, navigationIcon = {
            HBackIcon { goBack() }
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
            PageContent(viewModel = viewModel) { postUrl ->
                navToImages(postUrl)
            }
        }
    }
}


@Composable
fun PageContent(
    viewModel: SearchViewModel,
    onClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.stateFlow.collectAsState()

    uiState.error?.let {
        Toast.makeText(appContext, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (viewModel.canLoadMorePostsData() && !uiState.isLoading && listState.isScrolledToEnd()) {
            viewModel.getNextPosts()
        }
    }

    LazyColumn(
        state = listState
    ) {
        items(uiState.postItems) { post ->
            PostItemView(post) { postUrl ->
                onClick(postUrl)
            }
        }
        if (uiState.isLoading)
            item {
                HLoading()
            }
    }
}
