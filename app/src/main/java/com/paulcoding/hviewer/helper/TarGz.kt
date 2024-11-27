package com.paulcoding.hviewer.helper

import okhttp3.ResponseBody
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun extractTarGz(tarGzFile: File, destinationDir: File) {
    if (!destinationDir.exists()) {
        destinationDir.mkdirs()
    }

    try {
        val fileInputStream = FileInputStream(tarGzFile)
        val gzipInputStream = GzipCompressorInputStream(fileInputStream)
        val tarInputStream = TarArchiveInputStream(gzipInputStream)

        var entry: TarArchiveEntry? = tarInputStream.nextEntry
        while (entry != null) {
            val outputFile = File(destinationDir, entry.name)
            if (entry.isDirectory) {
                outputFile.mkdirs()
            } else {
                outputFile.parentFile?.mkdirs()
                val outputStream = FileOutputStream(outputFile)
                tarInputStream.copyTo(outputStream)
                outputStream.close()
            }
            entry = tarInputStream.nextEntry
        }

        tarInputStream.close()
        gzipInputStream.close()
        fileInputStream.close()

    } catch (e: IOException) {
        e.printStackTrace()
    }
}


fun extractTarGzFromResponseBody(responseBody: ResponseBody, destinationDir: File) {
    if (!destinationDir.exists()) {
        destinationDir.mkdirs()
    }

    val inputStream = responseBody.byteStream()
    val gzipInputStream = GzipCompressorInputStream(inputStream)
    val tarInputStream = TarArchiveInputStream(gzipInputStream)

    var directoryName = ""
    try {
        var entry: TarArchiveEntry? = tarInputStream.nextEntry
        while (entry != null) {
            val outputFile = File(destinationDir, entry.name)
            if (entry.isDirectory) {
                directoryName = entry.name.alsoLog("tar dirname")
                outputFile.mkdirs()
            } else {
                outputFile.parentFile?.mkdirs()
                val outputStream = FileOutputStream(outputFile)
                tarInputStream.copyTo(outputStream)
                outputStream.close()
            }
            entry = tarInputStream.nextEntry
        }

//        remove files in scripts directory before overwrite
        File(destinationDir, SCRIPTS_DIR).listFiles()?.forEach { it.delete() }
        File(destinationDir, directoryName).apply {
            if (exists()) {
                renameTo(File(destinationDir, SCRIPTS_DIR))
            }
        }
    } finally {
        tarInputStream.close()
        gzipInputStream.close()
        inputStream.close()
    }
}


