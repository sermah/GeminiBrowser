package dev.sermah.geminibrowser.model

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BrowserImpl(
    private val coroutineScope: CoroutineScope,
) : Browser {

    private val tabs = mutableMapOf<Int, TabBrowser>()
    private var tabCounter = 0

    private val _bookmarksFlow = MutableStateFlow(emptyList<Browser.Bookmark>())
    private val _tabsFlow = MutableStateFlow(emptyList<TabBrowser>())

    override val bookmarksFlow get() = _bookmarksFlow
    override val tabsFlow: StateFlow<List<TabBrowser>> get() = _tabsFlow

    override fun closeTab(id: Int): TabBrowser? =
        tabs.remove(id).also {
            it?.close() ?: Log.w(TAG, "Couldn't close tab $id (not found)")
            updateTabsFlow()
        }

    override fun createTab(url: String?): TabBrowser =
        TabBrowserImpl(coroutineScope).also {
            tabs[tabCounter++] = it
            url?.let { url -> it.openUrl(url) }
            updateTabsFlow()
        }

    override fun bookmarkUrl(url: String, title: String?) {
        TODO("Not yet implemented")
    }

    override fun unbookmarkUrl(id: Int) {
        TODO("Not yet implemented")
    }

    private fun updateTabsFlow() {
        _tabsFlow.value = tabs.values.toList()
    }

    companion object {
        private const val TAG = "BrowserImpl"
    }
}