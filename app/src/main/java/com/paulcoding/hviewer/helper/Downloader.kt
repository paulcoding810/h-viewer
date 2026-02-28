package com.paulcoding.hviewer.helper

import com.paulcoding.hviewer.exception.AppException
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import java.io.File

class Downloader(
    private val httpClient: HttpClient
) {
    suspend fun download(
        downloadUrl: String,
        destination: File,
    ): File {
        val input = httpClient
            .get(downloadUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.OctetStream)
            }
            .readRawBytes().inputStream()
        destination.outputStream().use { output ->
            input.copyTo(output)
        }
        return destination
    }

    suspend fun saveGz(tarUrl: String, destination: File) {
        try {
            val response = httpClient.get(tarUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.OctetStream)
            }
            val inputStream = response.readRawBytes().inputStream()
            extractTarGzFromResponseBody(inputStream, destination)
        } catch (e: Exception) {
            throw AppException.FailedToSaveGzException(e)
        }
    }
}
