package dev.sermah.geminibrowser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sermah.geminibrowser.InstanceProvider
import dev.sermah.geminibrowser.model.TabBrowser
import kotlinx.coroutines.CoroutineScope

class BrowserViewModel : ViewModel() {

    init {
        InstanceProvider.provide {
            single(CoroutineScope::class.java) { viewModelScope }
        }
    }

    private val tabBrowser = InstanceProvider[TabBrowser::class.java]

    val pageFlow = tabBrowser.pageFlow

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

    companion object {
        private const val TAG = "BrowserViewModel"
    }
}