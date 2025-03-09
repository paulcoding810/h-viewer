package com.paulcoding.hviewer.helper

import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageDownloader {
    private val client = OkHttpClient()

    suspend fun downloadImage(url: String, outputFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) return@withContext false

                val inputStream: InputStream? = response.body?.byteStream()
                val outputStream = FileOutputStream(outputFile)

                inputStream?.copyTo(outputStream)
                outputStream.close()
                inputStream?.close()

                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }
}
