package com.paulcoding.hviewer.ui.page.sites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.ui.icon.SettingsIcon
import com.paulcoding.hviewer.model.SiteConfigs

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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (siteConfigs.sites.keys.isEmpty()) {
                    Empty(navToSettings)
                } else
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

@Composable
private fun Empty(navToSettings: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(composition)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("No sites found")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Add repo?",
                modifier = Modifier.clickable { navToSettings() },
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
