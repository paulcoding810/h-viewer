package com.paulcoding.hviewer.network

import com.google.gson.Strictness
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.*

val ktorClient
    get() = HttpClient(Android) {
        install(ContentNegotiation) {
            gson {
                setStrictness(Strictness.LENIENT)
            }
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }
