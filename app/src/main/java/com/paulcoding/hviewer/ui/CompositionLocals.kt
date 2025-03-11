package com.paulcoding.hviewer.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.paulcoding.hviewer.model.SiteConfig

val LocalHostsMap = staticCompositionLocalOf<Map<String, SiteConfig>> { mapOf() }
