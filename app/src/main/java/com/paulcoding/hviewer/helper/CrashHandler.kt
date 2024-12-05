package com.paulcoding.hviewer.helper

import com.paulcoding.hviewer.MainApp.Companion.appContext
import java.io.File
import java.io.PrintWriter

// Reference: https://github.com/FooIbar/EhViewer/blob/main/app/src/main/kotlin/com/hippo/ehviewer/util/CrashHandler.kt
object CrashHandler {
    fun install() {
        val handler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            runCatching { saveCrashLog(e) }
            handler?.uncaughtException(t, e)
        }
    }

    private fun getThrowableInfo(t: Throwable, writer: PrintWriter) {
        t.printStackTrace(writer)
        var cause = t.cause
        while (cause != null) {
            cause.printStackTrace(writer)
            cause = cause.cause
        }
    }

    private fun saveCrashLog(e: Throwable) {
        val nowString = System.currentTimeMillis()
        val fileName = "crash-$nowString.log"
        val file = File(appContext.crashLogDir, fileName)

        runCatching {
            file.printWriter().use { writer ->
                writer.write("======== CrashInfo ========\n")
                getThrowableInfo(e, writer)
                writer.write("\n")
            }
        }.onFailure {
            log(it)
            file.delete()
        }
    }
}