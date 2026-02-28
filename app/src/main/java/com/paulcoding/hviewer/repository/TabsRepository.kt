package com.paulcoding.hviewer.repository

import com.paulcoding.hviewer.model.PostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TabsRepository {
    var tabs = MutableStateFlow(listOf<PostItem>())

    fun addTab(postItem: PostItem) {
        if (!tabs.value.contains(postItem))
            tabs.update {
                it + postItem
            }
    }

    fun removeTab(postItem: PostItem) {
        tabs.update {
            it - postItem
        }
    }

    fun clearTabs() {
        tabs.update {
            emptyList()
        }
    }
}