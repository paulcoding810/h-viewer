package com.paulcoding.hviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paulcoding.hviewer.ui.page.web.WebPage

class WebActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            WebPage(
                goBack = ::finish,
                url = intent.data.toString()
            )
        }
    }

    private fun finishWithResult(cookie: String, userAgent: String) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_COOKIE, cookie)
            putExtra(EXTRA_USER_AGENT, userAgent)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        const val EXTRA_COOKIE = "extra_cookie"
        const val EXTRA_USER_AGENT = "extra_user_agent"
    }
}


