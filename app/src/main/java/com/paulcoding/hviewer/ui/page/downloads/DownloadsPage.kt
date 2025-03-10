package com.paulcoding.hviewer.ui.page.downloads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.downloadDir
import com.paulcoding.hviewer.ui.component.HBackIcon
import com.paulcoding.hviewer.ui.component.HEmpty
import com.paulcoding.hviewer.ui.component.HIcon
import com.paulcoding.hviewer.ui.page.post.ImageModal
import com.paulcoding.hviewer.ui.page.post.PostImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsPage(
    goBack: () -> Unit,
    initialDir: String? = null,
) {
    var dirs by remember { mutableStateOf(emptyList<File>()) }
    var selectedDir by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(Unit) {
        downloadDir.listFiles()?.filter { it.isDirectory }?.toList()?.let {
            dirs = it
        }
        initialDir?.let {
            if (File(it).exists()) selectedDir = File(it)
        }
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.downloads)) }, navigationIcon = {
            HBackIcon { goBack() }
        }, actions = {
            if (selectedDir != null) HIcon(
                Icons.Outlined.Close,
                rounded = true,
            ) { selectedDir = null }
        })
    }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            if (selectedDir == null) LazyColumn(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dirs) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedDir = it
                        },
                    ) {
                        Icon(Icons.Outlined.Folder, it.name)
                        Text(
                            it.name, modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                if (dirs.isEmpty()) item {
                    HEmpty()
                }
            }
            else {
                ImageList(selectedDir!!)
            }
        }
    }
}

@Composable
internal fun ImageList(selectedDir: File) {
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var isSystemBarHidden by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val images by remember {
        derivedStateOf {
            selectedDir.listFiles()?.map { it.absolutePath } ?: emptyList()
        }
    }

    LazyColumn(
        state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images, key = { it }) { image ->
            PostImage(
                url = image,
                onDoubleTap = {
                    selectedImage = image
                },
                onTap = {
                    isSystemBarHidden = !isSystemBarHidden
                },
            )
        }
        if (images.isEmpty()) item {
            HEmpty()
        }
    }
    if (selectedImage != null) {
        ImageModal(url = selectedImage!!) {
            selectedImage = null
        }
    }
}