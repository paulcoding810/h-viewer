package com.paulcoding.hviewer.ui.page.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Javascript
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.page.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScriptPage(
    appViewModel: AppViewModel,
    type: String,
    goBack: () -> Unit,
    navToEditor: (String) -> Unit,
) {

    val listFiles = when (type) {
        "script" -> appViewModel.listScriptFiles
        "crash_log" -> appViewModel.listCrashLogFiles
        else -> emptyList()
    }

    val title = when (type) {
        "script" -> "List Script"
        "crash_log" -> "List Crash Log"
        else -> ""
    }

    val fileIcon = when (type) {
        "script" -> Icons.Outlined.Javascript
        "crash_log" -> Icons.Outlined.BugReport
        else -> return makeToast("Unknown type $type")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = title) },
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
            items(listFiles, key = { it.name }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable {
                            navToEditor(it.name)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        fileIcon,
                        contentDescription = "Javascript",
                        modifier = Modifier.size(32.dp),
                    )
                    Text(text = it.name)
                }
            }
        }
    }
}