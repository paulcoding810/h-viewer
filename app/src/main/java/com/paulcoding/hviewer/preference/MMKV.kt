package com.paulcoding.hviewer.preference

import com.tencent.mmkv.MMKV


const val REMOTE_URL = "remote_url"
const val BRANCH = "branch"
const val SECURE_SCREEN = "secure_screen"
const val PIN = "pin"
const val PIN_COUNT = "pin_count"
const val SHOWED_TUT_HIDE_IMAGE_MODAL = "showed_tut_hide_image_modal"

object Preferences {
    private val kv: MMKV = MMKV.defaultMMKV()

    fun getRemote() = kv.getString(REMOTE_URL, "")!!

    fun setRemote(url: String) {
        kv.putString(REMOTE_URL, url)
    }

    var branch: String
        get() = kv.getString(BRANCH, "main")!!
        set(value) {
            kv.putString(BRANCH, value)
        }

    var secureScreen: Boolean
        get() = kv.getBoolean(SECURE_SCREEN, true)
        set(value) {
            kv.putBoolean(SECURE_SCREEN, value)
        }

    var pinCount: Int
        get() = kv.getInt(PIN_COUNT, 4)
        set(value) {
            kv.putInt(PIN_COUNT, value)
        }

    var pin: String
        get() = kv.getString(PIN, "")!!
        set(value) {
            kv.putString(PIN, value)
        }

    var showedTutHideImageModal: Boolean
        get() = kv.getBoolean(SHOWED_TUT_HIDE_IMAGE_MODAL, false)
        set(value) {
            kv.putBoolean(SHOWED_TUT_HIDE_IMAGE_MODAL, value)
        }
}