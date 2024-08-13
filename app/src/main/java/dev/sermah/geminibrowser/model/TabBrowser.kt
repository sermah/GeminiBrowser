package dev.sermah.geminibrowser.model

import kotlinx.coroutines.flow.StateFlow

interface TabBrowser {
    val pageFlow: StateFlow<Page>
    val historyFlow: StateFlow<List<HistoryEntry>>
    val stateFlow: StateFlow<State>

    val historyIdx: Int
    val isClosed: Boolean

    fun openUrl(url: String)
    fun refresh()
    fun back()
    fun forward()
    fun stop()
    fun historyOpen(id: Int)

    fun close()

    data class Page(
        val url: String,
        val html: String,
        val code: Int,
        val message: String? = null,
        val body: String? = null,
    )

    data class HistoryEntry(
        val url: String,
        val title: String? = null,
    )

    data class State(
        val isLoading: Boolean,
        val canGoBack: Boolean,
        val canGoForward: Boolean,
    )
}