package dev.sermah.geminibrowser.texts

import dev.sermah.geminibrowser.model.network.GeminiClient

object ErrorTexts {
    fun title(throwable: Throwable) = when (throwable) {
        is GeminiClient.WrongProtocolException -> "Wrong address scheme"
        is GeminiClient.MalformedUriException -> "Malformed address"
        is GeminiClient.IncorrectHeaderException -> "Incorrect response header"
        is GeminiClient.ConnectionFailedException -> "Connection failed"
        is GeminiClient.ConnectionException -> "Connection error"
        else -> "Internal error"
    }

    fun description(throwable: Throwable) = when (throwable) {
        is GeminiClient.WrongProtocolException -> "The entered address has a wrong scheme/protocol. Try \"gemini://\""
        is GeminiClient.MalformedUriException -> "Check the entered address and try again."
        is GeminiClient.IncorrectHeaderException -> "The server has sent an incorrect header. Try again later or contact its owner."
        is GeminiClient.ConnectionFailedException -> "Couldn't reach the host, check your Internet connection and try again."
        is GeminiClient.ConnectionException -> "Something went wrong, check your Internet settings and try again."

        else -> "Something went wrong while loading the page."
    }
}