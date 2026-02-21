package com.paulcoding.hviewer.helper

import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

object ImageDownloader {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Downloads an image from the given URL to the specified output file.
     * 
     * @param context The coroutine context for execution
     * @param url The URL of the image to download
     * @param outputFile The file where the image will be saved
     * @return true if download was successful, false otherwise
     */
    suspend fun downloadImage(
        context: CoroutineContext, 
        url: String, 
        outputFile: File
    ): Boolean = withContext(context) {
        try {
            // Skip if file already exists and has content
            if (outputFile.exists() && outputFile.length() > 0) {
                return@withContext true
            }

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext false
                }

                val body = response.body ?: return@withContext false

                // Use buffered streams for better performance
                outputFile.outputStream().buffered().use { outputStream ->
                    body.byteStream().buffered().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Clean up partial download
            if (outputFile.exists()) {
                outputFile.delete()
            }
            return@withContext false
        }
    }
}
