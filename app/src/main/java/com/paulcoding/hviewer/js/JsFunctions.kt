package com.paulcoding.hviewer.js

import com.paulcoding.hviewer.MainApp.Companion.appContext
import com.paulcoding.hviewer.helper.readFile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

val fetchFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ): Any {
        val url = args.getOrNull(0) as? String ?: throw IllegalArgumentException("URL is required")
        val doc: Document = Jsoup.connect(url).get()
        return doc
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