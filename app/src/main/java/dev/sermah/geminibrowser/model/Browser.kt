package dev.sermah.geminibrowser.model

import kotlinx.coroutines.flow.StateFlow

interface Browser {
    val bookmarksFlow: StateFlow<List<Bookmark>>
    val tabsFlow: StateFlow<List<TabBrowser>>

    fun closeTab(id: Int): TabBrowser?
    fun createTab(url: String?): TabBrowser

    fun bookmarkUrl(url: String, title: String?)
    fun unbookmarkUrl(id: Int)

    class Bookmark(
        val id: Int,
        val url: String,
        val title: String? = null,
    )
}