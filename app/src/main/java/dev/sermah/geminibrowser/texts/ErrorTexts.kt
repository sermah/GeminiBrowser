package dev.sermah.geminibrowser.texts

import dev.sermah.geminibrowser.model.network.GeminiClient

object ErrorTexts {
    fun title(throwable: Throwable) = when (throwable) {
        is GeminiClient.WrongProtocolException -> "Wrong scheme"
        is GeminiClient.MalformedUriException -> "Malformed URI"
        is GeminiClient.WrongHeaderException -> "Wrong response header"
        is GeminiClient.ConnectionDisruptedException -> "Connection disrupted"
        is GeminiClient.ConnectionFailedException -> "Connection failed"
        else -> "Internal error"
    }

    fun description(throwable: Throwable) = when (throwable) {
        is GeminiClient.WrongProtocolException -> "URI has wrong scheme/protocol. Try gemini://"
        is GeminiClient.MalformedUriException -> "Check your address and try again."
        is GeminiClient.WrongHeaderException -> "The server has sent an incorrect header. Try again later."
        is GeminiClient.ConnectionDisruptedException -> "Connection has been disrupted, try again."
        is GeminiClient.ConnectionFailedException -> "Connection failed, try again."

        else -> "Encountered while loading a page."
    }
}