package com.paulcoding.hviewer.helper

import kotlinx.coroutines.flow.Flow

interface FavoriteHandler {
    val isFavorite: Flow<Boolean>
    fun toggle()
}