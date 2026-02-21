package com.paulcoding.hviewer.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

fun Activity.openInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
}