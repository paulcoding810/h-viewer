package com.paulcoding.hviewer.ui.page.post

import android.annotation.SuppressLint
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
import com.paulcoding.hviewer.helper.ImageDownloader
import com.paulcoding.hviewer.helper.SCRIPTS_DIR
import com.paulcoding.hviewer.helper.downloadDir
import com.paulcoding.hviewer.model.PostData
import com.paulcoding.hviewer.model.SiteConfig
import com.paulcoding.js.JS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class DownloadService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val channelId = "DownloadChannel"
    private val notificationId = 1

    private var postPage: Int = 1
    private var postTotalPage: Int = 1
    private var nextPage: String? = null
    private var images: MutableList<String> = mutableListOf()

    private fun reset() {
        postPage = 1
        postTotalPage = 1
        nextPage = null
        images = mutableListOf()

        _downloadStatusFlow.update { DownloadStatus.IDLE }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        reset()

        val postUrl = intent.getStringExtra("postUrl")
        val postName = intent.getStringExtra("postName")
        val siteConfig = intent.getParcelableExtra<SiteConfig>("siteConfig")
        if (postUrl != null && postName != null && siteConfig != null) {
            startForeground(notificationId, createNotification("Downloading $postName"))
            download(postUrl, postName, siteConfig)
        } else {
            throw IllegalArgumentException("Missing required parameters")
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun getImages(js: JS, url: String, page: Int = 1) {
        js.callFunction<PostData>("getImages", arrayOf(url, page)).onSuccess { postData ->
            postTotalPage = postData.total
            nextPage = postData.next
            images = (images + postData.images).toMutableList()
        }.onFailure {
            throw (it)
        }
    }

    private fun download(postUrl: String, postName: String, siteConfig: SiteConfig) {
        val js = JS(
            fileRelativePath = SCRIPTS_DIR + "/${siteConfig.scriptFile}",
            properties = mapOf("baseUrl" to siteConfig.baseUrl)
        )

        nextPage = postUrl

        try {
            // fetch image urls
            CoroutineScope(Dispatchers.IO).launch {
                _downloadStatusFlow.update { DownloadStatus.DOWNLOADING }
                while (postPage <= postTotalPage) {
                    delay(1000) // add some delay to avoid getting blocked by the server
                    nextPage?.let { getImages(js, it, postPage) }
                    updateNotification("Fetching ($postPage/$postTotalPage) pages")
                    postPage++
                }
                downloadImagesParallel(postName)
                _downloadStatusFlow.update { DownloadStatus.IDLE }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf() // stop the service if an exception occurs
        } finally {
            _downloadStatusFlow.update { DownloadStatus.IDLE }
        }
    }

    private suspend fun downloadImagesParallel(postName: String, onFinish: () -> Unit = {}) {
        val outputName = postName.trim().replace(Regex("[^\\p{L}0-9._]"), "_")
        coroutineScope {
            val outputDir = File(downloadDir, outputName).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            val paddingLength = images.size.toString().length
            val downloadJobs = images.mapIndexed { index, url ->
                async {
                    val file =
                        File(outputDir, "img_${index.toString().padStart(paddingLength, '0')}.jpg")

                    val success = ImageDownloader.downloadImage(url, file)
                    if (!success) {
                        println("❌ Failed to download: $url")
                    }
                }
            }
            var totalProgress = 0
            downloadJobs.chunked(5).forEach { chunkedJobs ->
                chunkedJobs.awaitAll()
                delay(500)
                totalProgress += chunkedJobs.size
                updateNotification("${totalProgress}/${images.size} images")
            }
            println("✅ All images downloaded successfully!")
            showDownloadCompleteNotification(outputDir)
            onFinish()
        }
    }


    private fun createNotification(title: String): Notification {
        notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(0, 0, true)
        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)
        return notification
    }

    private fun updateNotification(msg: String) {
        notificationBuilder.setContentText(msg)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId, "Download Service Channel", NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(
                serviceChannel
            )
        }
    }

    private fun showDownloadCompleteNotification(file: File) {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "hviewer://downloads/${Uri.encode(file.absolutePath)}".toUri(),
            this,
            MainActivity::class.java
        )

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }


        val completedNotification =
            NotificationCompat.Builder(this, channelId).setContentTitle("Download Complete")
                .setContentText("Tap to open")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(deepLinkPendingIntent)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(notificationId, completedNotification)
    }

    companion object {
        private val _downloadStatusFlow = MutableStateFlow(DownloadStatus.IDLE)
        val downloadStatusFlow = _downloadStatusFlow.asStateFlow()
    }
}

enum class DownloadStatus {
    IDLE,
    DOWNLOADING,
}