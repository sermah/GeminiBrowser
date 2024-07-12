package dev.sermah.geminibrowser.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.sermah.geminibrowser.AppDispatchers
import dev.sermah.geminibrowser.InstanceProvider
import dev.sermah.geminibrowser.model.GemHypertext
import dev.sermah.geminibrowser.model.network.GeminiClient
import dev.sermah.geminibrowser.model.network.GeminiClient.GeminiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserViewModel : ViewModel() {
    private val gemHypertext = InstanceProvider[GemHypertext::class.java]
    private val geminiClient = InstanceProvider[GeminiClient::class.java]
    private val _pageFlow = MutableStateFlow(Page("browser:start", "", 20))

    val pageFlow get() = _pageFlow

    fun openUrl(url: String, redirected: Boolean = false) {
        Log.d(TAG, "openUrl($url)")

        // TODO move to model
        CoroutineScope(AppDispatchers.Main).launch {
            val resp = geminiClient.get(url, pageFlow.value.url)

            when (resp) {
                is GeminiResponse.Success ->
                    _pageFlow.update {
                        Page(
                            url = url,
                            html = gemHypertext.convertToHypertext(resp.body),
                            code = resp.code,
                            message = resp.mimeType,
                            body = resp.body,
                        )
                    }

                is GeminiResponse.Redirect ->
                    if (!redirected)
                        openUrl(resp.uriReference, redirected = true)
                else -> {}
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