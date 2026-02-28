package com.paulcoding.hviewer.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.paulcoding.hviewer.MainActivity
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.model.PostData
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.js.JS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class DownloadImageService : Service() {
    // Service-scoped coroutine scope that will be cancelled when service is destroyed
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var js: JS? = null

    private var postPage: Int = 1
    private var postTotalPage: Int = 1
    private var nextPage: String? = null
    private var images: MutableList<String> = mutableListOf()

    private fun reset() {
        postPage = 1
        postTotalPage = 1
        nextPage = null
        images.clear()
        js = null
        _downloadStatusFlow.update { DownloadStatus.IDLE }
    }

    private fun stopService() {
        coroutineScope.cancel()
        reset()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_STOP_SERVICE) {
            stopService()
            return START_NOT_STICKY
        }

        reset()

        val postUrl = intent.getStringExtra(EXTRA_POST_URL)
        val postName = intent.getStringExtra(EXTRA_POST_NAME)
        val siteConfig = GlobalData.siteConfigMap[postUrl?.host]

        if (postUrl == null || postName == null || siteConfig == null) {
            showErrorNotification(getString(R.string.download_failed))
            stopSelf()
            return START_NOT_STICKY
        }

        // Start as foreground service
        val notification = createNotification(getString(R.string.downloading_images))
        startForeground(NOTIFICATION_ID, notification)

        // Start download
        download(postUrl, postName, siteConfig)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        js = null
        super.onDestroy()
    }

    private suspend fun getImages(url: String, page: Int = 1) {
        if (js == null) throw IllegalStateException("JS engine not initialized")
        js!!.callFunction<PostData>("getImages", arrayOf(url, page))
            .onSuccess { postData ->
                postTotalPage = postData.total
                nextPage = postData.next
                images.addAll(postData.images)
            }
            .onFailure {
                throw it
            }
    }

    private fun download(postUrl: String, postName: String, siteConfig: SiteConfig) {
        js = JS(
            fileRelativePath = "$SCRIPTS_DIR/${siteConfig.scriptFile}",
            properties = mapOf("baseUrl" to siteConfig.baseUrl)
        )

        nextPage = postUrl

        coroutineScope.launch {
            try {
                _downloadStatusFlow.update { DownloadStatus.DOWNLOADING }

                // Fetch image URLs
                while (postPage <= postTotalPage) {
                    delay(PAGE_FETCH_DELAY_MS)
                    nextPage?.let { getImages(it, postPage) }
                    updateNotification(getString(R.string.fetching_pages, postPage, postTotalPage))
                    postPage++
                }

                // Download images
                downloadImagesParallel(postName)

            } catch (e: Exception) {
                e.printStackTrace()
                showErrorNotification(getString(R.string.download_failed_error, e.localizedMessage))
            } finally {
                _downloadStatusFlow.update { DownloadStatus.IDLE }
                stopSelf()
            }
        }
    }

    private suspend fun downloadImagesParallel(postName: String) {
        val sanitizedName = postName.replace(Regex("[^\\p{L}0-9._]+"), " ").trim()

        coroutineScope {
            val outputDir = File(downloadDir, sanitizedName).apply {
                if (!exists() && !mkdirs()) {
                    throw IllegalStateException("Failed to create download directory")
                }
            }

            val totalImages = images.size
            val paddingLength = totalImages.toString().length

            val downloadJobs = images.mapIndexed { index, url ->
                async(start = CoroutineStart.LAZY) {
                    val file = File(outputDir, getImageFileName(url, index, paddingLength))
                    try {
                        ImageDownloader.downloadImage(coroutineContext, url, file)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }
            }

            var successCount = 0
            var failCount = 0

            downloadJobs.chunked(PARALLEL_DOWNLOAD_LIMIT).forEach { chunk ->
                val results = chunk.awaitAll()
                successCount += results.count { it }
                failCount += results.count { !it }

                delay(CHUNK_DELAY_MS)

                val progress = successCount + failCount
                updateNotification(getString(R.string.downloading_progress, progress, totalImages))
            }

            if (failCount > 0) {
                showDownloadCompleteWithErrorsNotification(outputDir, successCount, failCount)
            } else {
                showDownloadCompleteNotification(outputDir)
            }
        }
    }

    private fun getImageFileName(url: String, index: Int, paddingLength: Int): String {
        val extension = IMAGE_EXTENSIONS.firstOrNull { ext ->
            url.contains(".$ext", ignoreCase = true)
        } ?: DEFAULT_IMAGE_EXTENSION

        val paddedIndex = (index + 1).toString().padStart(paddingLength, '0')
        return "img_$paddedIndex.$extension"
    }

    private fun createNotification(title: String): Notification {
        val stopIntent = Intent(this, DownloadImageService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_delete,
                getString(R.string.cancel),
                stopPendingIntent
            )

        return notificationBuilder.build()
    }

    private fun updateNotification(message: String) {
        notificationBuilder.setContentText(message)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun showErrorNotification(errorMessage: String) {
        val errorNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.download_failed))
            .setContentText(errorMessage)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, errorNotification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.download_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.download_channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showDownloadCompleteNotification(file: File) {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "hviewer://downloads/${Uri.encode(file.absolutePath)}".toUri(),
            this,
            MainActivity::class.java
        )

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.download_complete))
            .setContentText(getString(R.string.tap_to_open))
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showDownloadCompleteWithErrorsNotification(
        file: File,
        successCount: Int,
        failCount: Int
    ) {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "hviewer://downloads/${Uri.encode(file.absolutePath)}".toUri(),
            this,
            MainActivity::class.java
        )

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.download_complete_with_errors))
            .setContentText(getString(R.string.download_summary, successCount, failCount))
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private val _downloadStatusFlow = MutableStateFlow(DownloadStatus.IDLE)
        val downloadStatusFlow = _downloadStatusFlow.asStateFlow()

        // Intent extras
        const val EXTRA_POST_URL = "postUrl"
        const val EXTRA_POST_NAME = "postName"

        // Actions
        const val ACTION_STOP_SERVICE = "STOP_FOREGROUND_SERVICE"

        // Notification
        private const val CHANNEL_ID = "DownloadChannel"
        private const val NOTIFICATION_ID = 1

        // Download configuration
        private const val PAGE_FETCH_DELAY_MS = 1000L
        private const val CHUNK_DELAY_MS = 500L
        private const val PARALLEL_DOWNLOAD_LIMIT = 5

        // Image extensions
        private val IMAGE_EXTENSIONS = listOf("webp", "png", "gif", "jpeg", "jpg")
        private const val DEFAULT_IMAGE_EXTENSION = "jpg"
    }
}

enum class DownloadStatus {
    IDLE,
    DOWNLOADING
}