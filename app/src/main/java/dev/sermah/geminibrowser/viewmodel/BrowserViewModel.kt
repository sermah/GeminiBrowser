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
    private val _htmlFlow = MutableStateFlow("")

    val htmlFlow get() = _htmlFlow

    fun openUrl(url: String) {
        Log.d(TAG, "openUrl($url)")

        CoroutineScope(AppDispatchers.Main).launch {
            val resp = geminiClient.get(url)

            when (resp) {
                is GeminiResponse.Success ->
                    _htmlFlow.update {
                        gemHypertext.convertToHypertext(resp.body)
                    }

                else -> {}
            }
        }
    }

    companion object {
        const val TAG = "BrowserViewModel"
    }
}