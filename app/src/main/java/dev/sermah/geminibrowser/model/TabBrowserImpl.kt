package dev.sermah.geminibrowser.model

import android.util.Log
import androidx.core.net.toUri
import dev.sermah.geminibrowser.AppDispatchers
import dev.sermah.geminibrowser.InstanceProvider
import dev.sermah.geminibrowser.model.network.GeminiClient
import dev.sermah.geminibrowser.model.network.GeminiClient.GeminiResponse
import dev.sermah.geminibrowser.util.relativizeUri
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TabBrowserImpl(
    private val coroutineScope: CoroutineScope
) : TabBrowser {
    private val gemHypertext = InstanceProvider[GemHypertext::class.java]
    private val geminiClient = InstanceProvider[GeminiClient::class.java]
    private val internalPagesProvider = InstanceProvider[InternalPagesProvider::class.java]

    private var pageLoadJob: Job? = null

    private val _pageFlow = MutableStateFlow(TabBrowser.Page("browser:start", "", 20))
    private val _historyFlow = MutableStateFlow(emptyList<TabBrowser.HistoryEntry>())
    private val _stateFlow = MutableStateFlow(TabBrowser.State(isLoading = false, canGoBack = false, canGoForward = false))

    private var _historyIdx = -1
    private var _isClosed = false

    override val pageFlow get() = _pageFlow.asStateFlow()
    override val historyFlow get() = _historyFlow.asStateFlow()
    override val historyIdx get() = _historyIdx
    override val isClosed get() = _isClosed
    override val stateFlow get() = _stateFlow.asStateFlow()

    override fun openUrl(url: String) {
        historyClearAfter(historyIdx)
        openUrlInternal(url = url, redirected = false, addToHistory = true)
    }

    private fun openUrlInternal(url: String, redirected: Boolean, addToHistory: Boolean) {
        val absoluteUrl = (pageFlow.value.url).relativizeUri(url)

        Log.d(TAG, "openUrl($url) -> $absoluteUrl")

        pageLoadJob?.cancel()
        updateState(isLoading = true)
        pageLoadJob = coroutineScope.launch(AppDispatchers.IO) {
            runCatching {
                geminiClient.get(absoluteUrl)
            }.onSuccess { response ->
                updateState(isLoading = false)

                when (response) {
                    is GeminiResponse.Success -> {
                        _pageFlow.update {
                            TabBrowser.Page(
                                url = absoluteUrl,
                                html = gemHypertext.convertToHypertext(response.body),
                                code = response.code,
                                message = response.mimeType,
                                body = response.body,
                            )
                        }
                        if (addToHistory) addToHistory(
                            TabBrowser.HistoryEntry(
                                title = absoluteUrl.toUri().host, // TODO Replace with smart title extractor
                                url = absoluteUrl,
                            )
                        )
                        Log.i(TAG, "[Success, ${response.code}] ${response.mimeType}")
                    }

                    is GeminiResponse.Redirect -> {
                        if (!redirected)
                            openUrlInternal(
                                url = response.uriReference,
                                redirected = true,
                                addToHistory = addToHistory,
                            )
                        Log.i(TAG, "[Redirect, ${response.code}] ${response.uriReference}")
                    }

                    else -> {
                        _pageFlow.update {
                            TabBrowser.Page(
                                url = absoluteUrl,
                                html = internalPagesProvider.getErrorPage(
                                    response.code,
                                    mapOf("uri" to absoluteUrl),
                                ),
                                code = response.code,
                            )
                        }
                        if (addToHistory) addToHistory(
                            TabBrowser.HistoryEntry(
                                title = "Error ${response.code}", // TODO Replace with smart title extractor (for errors)
                                url = absoluteUrl,
                            )
                        )
                        Log.w(TAG, "[Gemini error, ${response.code}]")
                    }
                }
            }.onFailure { err ->
                updateState(isLoading = false)

                if (err is CancellationException) {
                    Log.i(TAG, "[Stopped by stop(), ${err.javaClass.simpleName}] ${err.message}")
                    return@onFailure
                }

                _pageFlow.update {
                    TabBrowser.Page(
                        url = absoluteUrl,
                        html = internalPagesProvider.getErrorPage(
                            err,
                            mapOf("uri" to absoluteUrl),
                        ),
                        code = 0,
                    )
                }
                if (addToHistory) addToHistory(
                    TabBrowser.HistoryEntry(
                        title = "Internal Error", // TODO Replace with smart title extractor (for errors)
                        url = absoluteUrl,
                    )
                )
                Log.w(TAG, "[Internal error, ${err.javaClass.simpleName}] ${err.message}")
                Log.d(TAG, pageFlow.value.html)
            }
        }
    }

    override fun refresh() {
        openUrlInternal(
            url = pageFlow.value.url,
            redirected = false,
            addToHistory = false,
        )
    }

    override fun back() {
        val history = historyFlow.value

        if (historyIdx <= 0) {
            Log.w(TAG, "[back] History index is 0 or less, can't go back")
            return
        }

        val previousUrl = history[historyIdx - 1].url
        // TODO Do fast cache-assisted loading
        openUrlInternal(
            url = previousUrl,
            redirected = false,
            addToHistory = false,
        )

        _historyIdx--
        updateState(updateHistory = true)
    }

    override fun forward() {
        val history = historyFlow.value

        if (historyIdx == history.size - 1) {
            Log.w(TAG, "[forward] History index is already at the last entry, can't go forward")
            return
        }

        val nextUrl = history[historyIdx + 1].url
        // TODO Do fast cache-assisted loading
        openUrlInternal(
            url = nextUrl,
            redirected = false,
            addToHistory = false,
        )

        _historyIdx++
        updateState(updateHistory = true)
    }

    override fun stop() {
        pageLoadJob?.cancel()
    }

    override fun historyOpen(idx: Int) {
        val history = historyFlow.value

        if (idx < 0 || idx >= history.size) {
            Log.w(TAG, "[historyOpen] Requested history index is out of bounds ($idx/0..${history.size - 1}), can't proceed")
            return
        }

        val urlAtIdx = history[idx].url
        // TODO Do fast cache-assisted loading
        openUrlInternal(
            url = urlAtIdx,
            redirected = false,
            addToHistory = false,
        )

        _historyIdx = idx
    }

    override fun close() {
        pageLoadJob?.cancel()

        _isClosed = true
    }

    private fun historyClearAfter(idx: Int) {
        if (historyIdx >= idx) _historyIdx = idx

        _historyFlow.update {
            if (it.size > idx) {
                it.subList(0, idx + 1)
            } else {
                it
            }
        }
        updateState(updateHistory = true)
    }

    private fun addToHistory(entry: TabBrowser.HistoryEntry) {
        _historyFlow.update {
            it + entry
        }
        _historyIdx++
        updateState(updateHistory = true)
    }

    private fun updateState(isLoading: Boolean? = null, updateHistory: Boolean = false) {
        _stateFlow.update {
            TabBrowser.State(
                isLoading = isLoading ?: it.isLoading,
                canGoBack = if (updateHistory) historyIdx > 0 else it.canGoBack,
                canGoForward = if (updateHistory) historyIdx < historyFlow.value.size - 1 else it.canGoForward,
            )
        }
    }

    companion object {
        private const val TAG = "TabBrowserImpl"
    }
}