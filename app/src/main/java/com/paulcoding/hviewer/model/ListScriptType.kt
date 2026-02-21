package com.paulcoding.hviewer.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
enum class ListScriptType {
    Script,
    Crash
}