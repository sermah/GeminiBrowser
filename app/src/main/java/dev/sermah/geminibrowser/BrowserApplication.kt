package dev.sermah.geminibrowser

import android.app.Application
import dev.sermah.geminibrowser.InstanceProvider.get
import dev.sermah.geminibrowser.model.Browser
import dev.sermah.geminibrowser.model.BrowserImpl
import dev.sermah.geminibrowser.model.GemHypertext
import dev.sermah.geminibrowser.model.GemHypertextImpl
import dev.sermah.geminibrowser.model.GemtextParser
import dev.sermah.geminibrowser.model.GemtextParserImpl
import dev.sermah.geminibrowser.model.InternalPagesProvider
import dev.sermah.geminibrowser.model.InternalPagesProviderImpl
import dev.sermah.geminibrowser.model.network.GeminiClient
import dev.sermah.geminibrowser.model.network.GeminiClientImpl
import kotlinx.coroutines.CoroutineScope

class BrowserApplication : Application() {
    override fun onCreate() {
        provideStuff()

        super.onCreate()
    }

    private fun provideStuff() {
        InstanceProvider.provide {
            single(GeminiClient::class.java) { GeminiClientImpl() }
            single(GemtextParser::class.java) { GemtextParserImpl() }
            single(GemHypertext::class.java) { GemHypertextImpl(get(GemtextParser::class.java), convertHashtags = true) }
            single(InternalPagesProvider::class.java) { InternalPagesProviderImpl(convertHashtags = true) }

            many(Browser::class.java) { BrowserImpl(get(CoroutineScope::class.java)) }
        }
    }
}