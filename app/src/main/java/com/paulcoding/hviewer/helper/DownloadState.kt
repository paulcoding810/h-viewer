package com.paulcoding.hviewer.helper

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.ui.LocalHostsMap
import com.paulcoding.hviewer.ui.page.post.DownloadService
import com.paulcoding.hviewer.ui.page.post.DownloadStatus


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberDownloadState(post: PostItem): DownloadState {
    val hostsMap = LocalHostsMap.current

    val downloadState by DownloadService.downloadStatusFlow.collectAsState()
    val context = LocalContext.current

    val storagePermission =
        rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
            if (!granted)
                makeToast("Permission Denied!")
        }

    val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { granted ->
                if (!granted)
                    makeToast("Notification permission Denied!")
            }
        } else {
            null
        }

    fun checkPermissionOrDownload(block: () -> Unit) {
        if (notificationPermission != null && !notificationPermission.status.isGranted) {
            notificationPermission.launchPermissionRequest()
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q || storagePermission.status == PermissionStatus.Granted) {
            block()
        } else {
            storagePermission.launchPermissionRequest()
        }
    }

    fun download() {
        val siteConfig =
            post.getSiteConfig(hostsMap) ?: throw Exception("Site config not found: ${post.url}")

        checkPermissionOrDownload {
            makeToast(context.getString(R.string.downloading_post, post.name))
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra("postUrl", post.url)
                putExtra("postName", post.name)
                putExtra("siteConfig", siteConfig)
            }
            context.startForegroundService(intent)
        }
    }

    return DownloadState(
        status = downloadState,
        isDownloading = downloadState == DownloadStatus.DOWNLOADING,
        download = ::download
    )
}

class DownloadState(
    val status: DownloadStatus,
    val isDownloading: Boolean,
    val download: () -> Unit
)
