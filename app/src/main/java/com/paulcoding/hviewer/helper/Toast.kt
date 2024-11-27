package com.paulcoding.hviewer.helper

import android.widget.Toast
import com.paulcoding.hviewer.MainApp.Companion.appContext

fun makeToast(message: String?) {
    if (message == null) return

    Toast.makeText(
        appContext,
        message,
        Toast.LENGTH_SHORT
    ).show()
}