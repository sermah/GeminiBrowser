package dev.sermah.geminibrowser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sermah.geminibrowser.InstanceProvider
import dev.sermah.geminibrowser.model.Browser
import dev.sermah.geminibrowser.model.TabBrowser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BrowserViewModel : ViewModel() {

    init { // Only for InstanceProvider
        InstanceProvider.provide {
            single(CoroutineScope::class.java) { viewModelScope }
        }
    }

    private val browser = InstanceProvider[Browser::class.java]
    private var tabBrowser = browser.createTab("gemini://geminiprotocol.net/")

    private var _pageFlow = tabBrowser.pageFlow
    val pageFlow: StateFlow<TabBrowser.Page> = _pageFlow

    init {
        browser.tabsFlow.onEach { tabs ->
            if (tabBrowser.isClosed) {
                tabBrowser = tabs.firstNotClosedTab() ?: browser.createTab("gemini://geminiprotocol.net/")
            }
            _pageFlow = tabBrowser.pageFlow
        }.launchIn(viewModelScope)
    }

    fun openUrl(url: String) {
        tabBrowser.openUrl(url)
    }

    fun refresh() {
        tabBrowser.refresh()
    }

    fun back() {
        tabBrowser.back()
    }

    fun forward() {
        tabBrowser.forward()
    }

    fun stop() {
        tabBrowser.stop()
    }

    private fun List<TabBrowser>.firstNotClosedTab(): TabBrowser? = find { !it.isClosed }

    companion object {
        private const val TAG = "BrowserViewModel"
    }
}