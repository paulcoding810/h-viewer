package com.paulcoding.hviewer.ui.page.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.model.ListScriptType
import com.paulcoding.hviewer.ui.component.HBackIcon
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScriptPage(
    viewModel: ListScriptViewModel = koinViewModel(),
    goBack: () -> Unit,
    navToEditor: (ListScriptType, String) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.title) },
                navigationIcon = {
                    HBackIcon {
                        goBack()
                    }
                })
        }) { paddings ->

        LazyColumn(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            items(uiState.files, key = { it.name }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable {
                            navToEditor(uiState.type, it.name)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        uiState.icon,
                        contentDescription = "Javascript",
                        modifier = Modifier.size(32.dp),
                    )
                    Text(text = it.name)
                }
            }
        }
    }
}