package com.paulcoding.hviewer.preference

import com.tencent.mmkv.MMKV


const val REMOTE_URL = "remote_url"
const val SECURE_SCREEN = "secure_screen"

object Preferences {
    private val kv: MMKV = MMKV.defaultMMKV()

    fun getRemote() = kv.getString(REMOTE_URL, "")!!

    fun setRemote(url: String) {
        kv.putString(REMOTE_URL, url)
    }

    var secureScreen: Boolean
        get() = kv.getBoolean(SECURE_SCREEN, true)
        set(value) {
            kv.putBoolean(SECURE_SCREEN, value)
        }
}