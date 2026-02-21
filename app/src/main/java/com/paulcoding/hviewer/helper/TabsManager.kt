package com.paulcoding.hviewer.helper

import com.paulcoding.hviewer.model.PostItem
import com.paulcoding.hviewer.repository.TabsRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class TabsManager(
    private val tabsRepository: TabsRepository,
) {
    val tabsSizeFlow = tabsRepository.tabs
        .map { it.size }
        .distinctUntilChanged()

    fun addTab(postItem: PostItem) {
        tabsRepository.addTab(postItem)
    }
}

