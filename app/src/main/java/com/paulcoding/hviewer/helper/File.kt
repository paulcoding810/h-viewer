package com.paulcoding.hviewer.helper

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream

const val SCRIPTS_DIR = "scripts"
const val CRASH_LOG_DIR = "crash_logs"
const val CONFIG_FILE = "config.json"

val Context.scriptsDir
    get() = File(filesDir, SCRIPTS_DIR)

val Context.crashLogDir
    get() = File(filesDir, CRASH_LOG_DIR)

val Context.configFile
    get() = File(scriptsDir, CONFIG_FILE)

val downloadDir: File = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "HViewer"
)

fun Context.setupPaths() {
    scriptsDir.mkdir()
    crashLogDir.mkdir()

    if (!downloadDir.exists()) {
        downloadDir.mkdirs()
        val nomediaFile = File(downloadDir, ".nomedia")
        if (!nomediaFile.exists()) {
            nomediaFile.createNewFile()
        }
    }
}

fun Context.writeFile(data: String, fileName: String, fileDir: File = scriptsDir): File {
    val file = File(fileDir, fileName)
    FileOutputStream(file).use { fos ->
        fos.write(data.toByteArray())
    }
    return file
}

fun Context.readFile(fileName: String, fileDir: File = scriptsDir): String {
    val file = File(fileDir, fileName)

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

inline fun <reified T> Context.readConfigFile(): Result<T> {
    return runCatching {
        val content = readFile(CONFIG_FILE)
        val type = object : TypeToken<T>() {}.type
        Gson().fromJson(content, type) as T
    }
}
