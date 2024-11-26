package com.paulcoding.hviewer.js

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import java.io.FileReader

fun toJsonElement(jsObject: Any?, gson: Gson = Gson()): JsonElement {
    return when (jsObject) {
        is NativeObject -> {
            val map = jsObject.entries.associate { it.key.toString() to it.value }
            gson.toJsonTree(map)
        }

        is NativeArray -> {
            val list = jsObject.toArray().toList()
            gson.toJsonTree(list)
        }

        else -> gson.toJsonTree(jsObject)
    }
}

class JS(envFilePath: String) {
    var scope: ScriptableObject
    val gson = Gson()

    fun prepareContext(): Context {
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        context.setGeneratingDebug(BuildConfig.DEBUG)

        return context
    }

    init {
        val context = prepareContext()
        scope = context.initStandardObjects()

        ScriptableObject.putProperty(scope, "import", importFunction)
        ScriptableObject.putProperty(scope, "fetch", fetchFunction)
        ScriptableObject.putProperty(scope, "atob", atobFunction)
        ScriptableObject.putProperty(scope, "console", NativeObject().apply {
            put("log", this, logFunction)
        })

        val reader = FileReader(envFilePath)
        context.evaluateReader(scope, reader, envFilePath, 1, null)
        Context.exit()
        reader.close()
    }

    // <reified> is required (https://github.com/google/gson/blob/main/Troubleshooting.md#-illegalargumentexception-typetoken-type-argument-must-not-contain-a-type-variable)
    suspend inline fun <reified T> evaluateString(script: String, sourceName: String): Result<T> {
        return withContext(Dispatchers.IO) {
            return@withContext runCatching {
                val context = prepareContext()
                val result = context.evaluateString(scope, script, sourceName, 1, null)
                transformResult<T>(result)
            }.closeContext()
        }
    }

    suspend inline fun <reified T> callFunction(
        functionName: String,
        args: Array<Any> = arrayOf()
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            return@withContext runCatching {
                val fn = scope.get(functionName, scope) as Function
                if (fn == Scriptable.NOT_FOUND)
                    throw Exception("$functionName not found.")
                val result = fn.call(prepareContext(), scope, scope, args)
                transformResult<T>(result)
            }.closeContext()
        }
    }

    inline fun <reified T> transformResult(result: Any): T {
        val jsonElement = toJsonElement(result)
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(jsonElement, type) as T
    }
}

fun <T> Result<T>.closeContext(): Result<T> {
    onSuccess {
        Context.exit()
    }
    onFailure {
        Context.exit()
    }

    return this
}
