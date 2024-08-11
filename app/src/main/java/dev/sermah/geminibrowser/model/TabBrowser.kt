package dev.sermah.geminibrowser.model

import kotlinx.coroutines.flow.StateFlow

interface TabBrowser {
    val pageFlow: StateFlow<Page>
    val historyFlow: StateFlow<List<HistoryEntry>>
    val bookmarksFlow: StateFlow<List<Bookmark>>

    fun openUrl(url: String)
    fun refresh()
    fun back()
    fun forward()
    fun stop()
    fun bookmarkUrl(url: String, title: String?)
    fun unbookmarkUrl(id: Int)
    fun openFromHistory(id: Int)

    class Page(
        val url: String,
        val html: String,
        val code: Int,
        val message: String? = null,
        val body: String? = null,
    )

    class HistoryEntry(
        val id: Int,
        val url: String,
        val title: String? = null,
    )

    class Bookmark(
        val id: Int,
        val url: String,
        val title: String? = null,
    )
}