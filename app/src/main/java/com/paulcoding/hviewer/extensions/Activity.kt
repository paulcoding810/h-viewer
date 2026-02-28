package com.paulcoding.hviewer.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.paulcoding.hviewer.WebActivity

fun Activity.openInBrowser(url: String) {
    val intent = Intent(this, WebActivity::class.java).apply {
        data = url.toUri()
    }
    this.startActivity(intent)
}

fun Activity.shareText(text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    // Always show the chooser
    val shareIntent = Intent.createChooser(sendIntent, null)

    // Ensure there's an app to handle the intent before starting
    if (sendIntent.resolveActivity(packageManager) != null) {
        startActivity(shareIntent)
    }
}

fun Activity.copyText(text: String) {
    val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("HViewer URL", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}