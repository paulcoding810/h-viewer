package com.paulcoding.hviewer.ui.page

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.paulcoding.hviewer.extensions.serializableType
import com.paulcoding.hviewer.model.ListScriptType
import com.paulcoding.hviewer.model.PostItem
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

object Routes {
    @Serializable
    @Immutable
    object Sites

    @Serializable
    @Immutable
    data class Site(
        val url: String,
        val isSearch: Boolean = false
    )

    @Serializable
    @Immutable
    // Must have url property as this use PostsViewModel and it seeks for Site(url)
    data class CustomTag(val url: String, val name: String)

    @Serializable
    @Immutable
    data class Post(val postItem: PostItem) {
        companion object {
            val typeMap =
                //mapOf(typeOf<PostItem>() to PostNavType())
                mapOf(typeOf<PostItem>() to serializableType<PostItem>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<Post>(typeMap)
        }
    }

    @Serializable
    @Immutable
    data class Search(val url: String)

    @Serializable
    @Immutable
    data class ListScript(val type: ListScriptType)

    @Serializable
    @Immutable
    data class Script(val scriptId: String)

    @Serializable
    @Immutable
    object Settings

    @Serializable
    @Immutable
    object Favorite

    @Serializable
    @Immutable
    data class Editor(
        val type: ListScriptType,
        val fileName: String,
    )

    @Serializable
    @Immutable
    object History

    @Serializable
    @Immutable
    data class WebView(val url: String)

    @Serializable
    @Immutable
    object Tabs

    @Serializable
    @Immutable
    object Downloads
}