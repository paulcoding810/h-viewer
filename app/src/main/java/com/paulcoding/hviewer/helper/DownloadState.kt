package com.paulcoding.hviewer.helper

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.DownloadImageService.Companion.EXTRA_POST_NAME
import com.paulcoding.hviewer.helper.DownloadImageService.Companion.EXTRA_POST_URL
import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.model.SiteConfig

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberDownloadState(post: PostItem, siteConfig: SiteConfig): DownloadState {
    val downloadState by DownloadImageService.downloadStatusFlow.collectAsState()
    val context = LocalContext.current

    val storagePermission =
        rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
            if (!granted) {
                makeToast(context.getString(R.string.permission_denied))
            }
        }

    val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { granted ->
                if (!granted) {
                    makeToast(context.getString(R.string.notification_permission_denied))
                }
            }
        } else {
            null
        }

    fun checkPermissionOrDownload(block: () -> Unit) {
        if (notificationPermission != null && !notificationPermission.status.isGranted) {
            notificationPermission.launchPermissionRequest()
            return
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q || storagePermission.status == PermissionStatus.Granted) {
            block()
        } else {
            storagePermission.launchPermissionRequest()
        }
    }

    fun download() {
        checkPermissionOrDownload {
            makeToast(context.getString(R.string.downloading_post, post.name))
            val intent = Intent(context, DownloadImageService::class.java).apply {
                putExtra(EXTRA_POST_URL, post.url)
                putExtra(EXTRA_POST_NAME, post.name)
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

/**
 * Compatibility function for cases where siteConfig is not available.
 * Derives siteConfig from the post URL.
 */
@Composable
fun rememberDownloadState(post: PostItem): DownloadState {
    val postUri = post.url.toUri()
    val host = postUri.host ?: throw IllegalArgumentException("Invalid post URL: ${post.url}")
    val siteConfig = GlobalData.siteConfigMap[host]
        ?: throw IllegalStateException("No site config found for host: $host")

    return rememberDownloadState(post, siteConfig)
}

/**
 * Represents the state of a download operation.
 */
data class DownloadState(
    val status: DownloadStatus,
    val isDownloading: Boolean,
    val download: () -> Unit
)
