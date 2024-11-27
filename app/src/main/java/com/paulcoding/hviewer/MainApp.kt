package com.paulcoding.hviewer

import android.app.Application
import android.content.Context
import com.paulcoding.hviewer.helper.setupPaths
import com.tencent.mmkv.MMKV

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        MMKV.initialize(this)

        setupPaths()
    }

    companion object {
        lateinit var appContext: Context
    }
}