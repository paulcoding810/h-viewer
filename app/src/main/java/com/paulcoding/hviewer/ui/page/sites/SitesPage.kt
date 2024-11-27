package com.paulcoding.hviewer.ui.page.sites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.ui.icon.SettingsIcon
import com.paulcoding.hviewer.ui.model.SiteConfigs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesPage(
    navToTopics: (site: String) -> Unit,
    goBack: () -> Unit,
    siteConfigs: SiteConfigs,
    navToSettings: () -> Unit
) {

    Scaffold(topBar = {
        TopAppBar(title = { Text("Sites") }, actions = {
            IconButton(onClick = navToSettings) {
                Icon(SettingsIcon, "Settings")
            }
        })
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                siteConfigs.sites.keys.map { site ->
                    Box(modifier = Modifier.clickable {
                        navToTopics(site)
                    }) {
                        Text(site)
                    }
                }
            }
        }
    }
}
