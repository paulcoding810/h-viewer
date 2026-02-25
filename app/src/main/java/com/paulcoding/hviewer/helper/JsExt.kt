package com.paulcoding.hviewer.helper

import com.paulcoding.js.JS

suspend fun JS.getTitle(url: String) = evaluateString<String>(
    """
                let data = fetch("$url")
                let html = data.html()
                String(html.title()) 
            """.trimIndent()
)
