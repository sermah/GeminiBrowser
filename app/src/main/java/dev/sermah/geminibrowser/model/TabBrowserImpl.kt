package dev.sermah.geminibrowser.model

import android.util.Log
import dev.sermah.geminibrowser.AppDispatchers
import dev.sermah.geminibrowser.InstanceProvider
import dev.sermah.geminibrowser.model.network.GeminiClient
import dev.sermah.geminibrowser.model.network.GeminiClient.GeminiResponse
import dev.sermah.geminibrowser.util.relativizeUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TabBrowserImpl(
    private val coroutineScope: CoroutineScope
) : TabBrowser {
    private val gemHypertext = InstanceProvider[GemHypertext::class.java]
    private val geminiClient = InstanceProvider[GeminiClient::class.java]
    private val internalPagesProvider = InstanceProvider[InternalPagesProvider::class.java]

    private val _pageFlow = MutableStateFlow(TabBrowser.Page("browser:start", "", 20))
    private val _historyFlow = MutableStateFlow(emptyList<TabBrowser.HistoryEntry>())
    private val _bookmarksFlow = MutableStateFlow(emptyList<TabBrowser.Bookmark>())

    private var pageLoadJob: Job? = null

    override val pageFlow get() = _pageFlow
    override val historyFlow get() = _historyFlow
    override val bookmarksFlow get() = _bookmarksFlow

    override fun openUrl(url: String) = openUrlInternal(url)

    private fun openUrlInternal(url: String, redirected: Boolean = false) {
        val absoluteUrl = (pageFlow.value.url).relativizeUri(url)

        Log.d(TAG, "openUrl($url) -> $absoluteUrl")

        // TODO move to model
        pageLoadJob?.cancel()
        pageLoadJob = coroutineScope.launch(AppDispatchers.IO) {
            runCatching {
                geminiClient.get(absoluteUrl)
            }.onSuccess { response ->
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
                        Log.d(TAG, "[Success, ${response.code}] ${response.mimeType}")
                    }

                    is GeminiResponse.Redirect -> {
                        if (!redirected)
                            openUrl(response.uriReference)
                        Log.d(TAG, "[Redirect, ${response.code}] ${response.uriReference}")
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
                        Log.d(TAG, "[Gemini error, ${response.code}]")
                    }
                }
            }.onFailure { err ->
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
                Log.d(TAG, "[Internal error, ${err.javaClass.simpleName}] ${err.message}")
                Log.d(TAG, pageFlow.value.html)
            }
        }
    }

    override fun refresh() {
        openUrl(pageFlow.value.url)
    }

    override fun back() {
        TODO("Not yet implemented")
    }

    override fun forward() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun bookmarkUrl(url: String, title: String?) {
        TODO("Not yet implemented")
    }

    override fun unbookmarkUrl(id: Int) {
        TODO("Not yet implemented")
    }

    override fun openFromHistory(id: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "TabBrowserImpl"
    }
}