package com.paulcoding.hviewer.network

import com.paulcoding.hviewer.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    //// Allows unquoted keys and values, and other minor malformations not strictly compliant with the RFC spec.
                    //isLenient = true
                    //// Ignores fields in the JSON payload that are not present in your Kotlin data class.
                    //ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = if (BuildConfig.DEBUG) {
                    LogLevel.BODY
                } else {
                    LogLevel.NONE
                }

                filter { request ->
                    val contentType = request.headers[HttpHeaders.ContentType]
                    contentType != ContentType.Application.OctetStream.toString()
                }
            }
        }
    }
}