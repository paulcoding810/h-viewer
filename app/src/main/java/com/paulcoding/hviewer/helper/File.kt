package com.paulcoding.hviewer.helper

import android.content.Context
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun Context.writeFile(data: String, fileName: String): File {
    openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
        outputStream.use { stream ->
            stream.write(data.toByteArray())
        }
    }

    return File(filesDir, fileName)
}

fun Context.readFile(path: String): String {
    val stringBuilder = StringBuilder()

    try {
        openFileInput(path).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return stringBuilder.toString()
}

inline fun <reified T> Context.readJsonFile(path: String): Result<T> {
    return runCatching {
        val content = readFile(path)
        return@runCatching Gson().fromJson(content, T::class.java)
    }
}