package com.paulcoding.hviewer

import android.app.Application
import android.content.Context
import com.paulcoding.hviewer.helper.CrashHandler
import com.paulcoding.hviewer.helper.setupPaths
import com.paulcoding.js.JS
import com.tencent.mmkv.MMKV

class MainApp : Application() {
    override fun onCreate() {
        CrashHandler.install()
        super.onCreate()
        appContext = this
        MMKV.initialize(this)
        JS.initialize(this)

        setupPaths()
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}