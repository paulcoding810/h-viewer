package com.paulcoding.hviewer.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri

fun Activity.openInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}