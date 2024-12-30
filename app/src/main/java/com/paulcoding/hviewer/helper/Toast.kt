package com.paulcoding.hviewer.helper

import android.widget.Toast
import androidx.annotation.StringRes
import com.paulcoding.hviewer.MainApp.Companion.appContext

fun makeToast(message: String?) {
    if (message == null) return

    Toast.makeText(
        appContext,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun makeToast(@StringRes stringId: Int) {
    Toast.makeText(
        appContext,
        appContext.getString(stringId),
        Toast.LENGTH_SHORT
    ).show()
}