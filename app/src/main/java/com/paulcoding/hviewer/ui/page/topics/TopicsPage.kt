package com.paulcoding.hviewer.ui.page.topics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.hviewer.ui.component.HBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsPage(
    navToTopic: (topic: String) -> Unit,
    goBack: () -> Unit,
    siteConfig: SiteConfig
) {

    val tags = siteConfig.tags.keys.toList()

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.topics)) }, navigationIcon = {
            HBackIcon {
                goBack()
            }
        })
    }) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tags) { tag ->
                    Topic(tag) {
                        navToTopic(tag)
                    }
                }
            }
        }
    }
}

@Composable
fun Topic(tag: String, onClick: () -> Unit) {
    Box(modifier = Modifier
        .clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(tag)
    }
}
