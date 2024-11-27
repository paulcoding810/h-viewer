package com.paulcoding.hviewer.helper

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

 const val SCRIPTS_DIR = "scripts"

private val Context.scriptsDir
    get() = File(filesDir, SCRIPTS_DIR)

fun Context.setupPaths() {
    scriptsDir.mkdir()
}

fun Context.writeFile(data: String, fileName: String): File {
    val file = File(scriptsDir, fileName)
    FileOutputStream(file).use { fos ->
        fos.write(data.toByteArray())
    }
    return file
}

fun Context.readFile(fileName: String): String {
    val file = File(scriptsDir, fileName)

    file.bufferedReader().use { reader ->
        return reader.readText()
    }
}

inline fun <reified T> Context.readJsonFile(fileName: String): Result<T> {
    return runCatching {
        val content = readFile(fileName)
        return@runCatching Gson().fromJson(content, T::class.java)
    }
}
