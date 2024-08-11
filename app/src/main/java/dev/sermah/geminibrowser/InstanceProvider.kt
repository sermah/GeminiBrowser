package dev.sermah.geminibrowser

import dev.sermah.geminibrowser.model.GemHypertext
import dev.sermah.geminibrowser.model.GemHypertextImpl
import dev.sermah.geminibrowser.model.GemtextParser
import dev.sermah.geminibrowser.model.GemtextParserImpl
import dev.sermah.geminibrowser.model.InternalPagesProvider
import dev.sermah.geminibrowser.model.InternalPagesProviderImpl
import dev.sermah.geminibrowser.model.network.GeminiClient
import dev.sermah.geminibrowser.model.network.GeminiClientImpl

object InstanceProvider {
    private val map = mapOf<Class<out Any>, Lazy<Any>>(
        GeminiClient::class.java to lazy { GeminiClientImpl() },
        GemtextParser::class.java to lazy { GemtextParserImpl() },
        GemHypertext::class.java to lazy { GemHypertextImpl(this[GemtextParser::class.java], convertHashtags = true) },
        InternalPagesProvider::class.java to lazy { InternalPagesProviderImpl(convertHashtags = true) }
    )

    operator fun <T> get(klass: Class<T>): T = map[klass]?.value as T
}