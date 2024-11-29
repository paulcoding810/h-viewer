package com.paulcoding.hviewer.js

import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.helper.log
import com.paulcoding.hviewer.helper.readFile
import com.paulcoding.hviewer.network.ktorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeJSON
import org.mozilla.javascript.Scriptable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val fetchFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ): Any? {
        val url = args.getOrNull(0) as? String
        if (url.isNullOrEmpty())
            throw IllegalArgumentException("URL is required")

        return runCatching {
            Jsoup.connect(url).followRedirects(true).get()
        }.onSuccess {
            return it
        }.onFailure {
            log(url, "Failed to fetch")
            it.printStackTrace()
            return null
        }
    }
}

val xhrFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any>
    ): Any {
        val url = args.getOrNull(0) as? String
        if (url.isNullOrEmpty())
            throw IllegalArgumentException("URL is required")

        return runBlocking {
            ktorClient.use { client ->
                val res: String = client.get(url).body()
                NativeJSON.parse(
                    cx, scope, res
                ) { cx, scope, thisObj, args -> args[1] }
            }
        }
    }
}

val logFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ): Any? {
        print("[JS LOG]: ")
        args.forEach { arg ->
            println(Context.toString(arg))
        }
        return Context.getUndefinedValue()
    }
}

val importFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ) {
        val filePath = args.getOrNull(0) as? String
            ?: throw IllegalArgumentException("File path is required")

        val script = appContext.readFile(filePath)
        cx?.evaluateString(scope, script, filePath, 1, null)
    }
}

@OptIn(ExperimentalEncodingApi::class)
val atobFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any>
    ): Any {
        val encoded = args.getOrNull(0) as? String
            ?: throw IllegalArgumentException("Encoded string is required")
        val decoded = Base64.Default.decode(encoded)
        return decoded.decodeToString()
    }
}