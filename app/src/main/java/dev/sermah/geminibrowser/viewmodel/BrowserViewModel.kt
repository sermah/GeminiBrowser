package dev.sermah.geminibrowser.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.sermah.geminibrowser.AppDispatchers
import dev.sermah.geminibrowser.InstanceProvider
import dev.sermah.geminibrowser.model.GemHypertext
import dev.sermah.geminibrowser.model.InternalPagesProvider
import dev.sermah.geminibrowser.model.network.GeminiClient
import dev.sermah.geminibrowser.model.network.GeminiClient.GeminiResponse
import dev.sermah.geminibrowser.util.relativizeUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserViewModel : ViewModel() {
    private val gemHypertext = InstanceProvider[GemHypertext::class.java]
    private val geminiClient = InstanceProvider[GeminiClient::class.java]
    private val internalPagesProvider = InstanceProvider[InternalPagesProvider::class.java]
    private val _pageFlow = MutableStateFlow(Page("browser:start", "", 20))

    val pageFlow get() = _pageFlow

    fun openUrl(url: String, redirected: Boolean = false) {
        val absoluteUrl = (pageFlow.value.url).relativizeUri(url)

        Log.d(TAG, "openUrl($url) -> $absoluteUrl")

        // TODO move to model
        CoroutineScope(AppDispatchers.IO).launch {
            runCatching {
                geminiClient.get(absoluteUrl)
            }.onSuccess { response ->
                when (response) {
                    is GeminiResponse.Success -> {
                        _pageFlow.update {
                            Page(
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
                            openUrl(response.uriReference, redirected = true)
                        Log.d(TAG, "[Redirect, ${response.code}] ${response.uriReference}")
                    }

                    else -> {
                        _pageFlow.update {
                            Page(
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
                    Page(
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

    class Page(
        val url: String,
        val html: String,
        val code: Int,
        val message: String? = null,
        val body: String? = null,
    )

    companion object {
        const val TAG = "BrowserViewModel"
    }
}