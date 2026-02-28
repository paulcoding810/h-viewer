package com.paulcoding.hviewer.helper

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
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

inline fun <reified T> Context.readJsonFile(fileName: String): Result<T> = runCatching {
    val content = readFile(fileName)
    Json.decodeFromString(content)
}

suspend inline fun <reified T> Context.readConfigFile(): T = withContext(Dispatchers.IO) {
    if (!configFile.exists())
        throw (FileNotFoundException(configFile.absolutePath))
    val content = readFile(CONFIG_FILE)
    Json.decodeFromString(content)
}
