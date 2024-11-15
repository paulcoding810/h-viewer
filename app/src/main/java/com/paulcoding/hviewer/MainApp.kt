package com.paulcoding.hviewer

import android.app.Application
import android.content.Context

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        lateinit var appContext: Context
    }
}