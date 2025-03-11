package com.paulcoding.hviewer.helper

import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

object ImageDownloader {
    private val client = OkHttpClient()

    suspend fun downloadImage(context: CoroutineContext, url: String, outputFile: File): Boolean {
        return withContext(context) {
            println("🔵 Downloading image: $url")
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return@withContext false

            val inputStream: InputStream? = response.body?.byteStream()
            val outputStream = FileOutputStream(outputFile)

            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()

            return@withContext true
        }
    }
}
