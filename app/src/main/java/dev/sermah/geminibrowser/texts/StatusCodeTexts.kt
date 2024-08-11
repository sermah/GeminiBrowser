package dev.sermah.geminibrowser.texts

import dev.sermah.geminibrowser.model.network.GeminiClient.GeminiResponse.StatusCode

object StatusCodeTexts {
    fun title(code: Int) = when (code) {
        StatusCode.INPUT.code -> "Input requested"
        StatusCode.SENSITIVE_INPUT.code -> "Sensitive input requested"
        StatusCode.SUCCESS.code -> "Success"
        StatusCode.TEMP_REDIRECTION.code -> "Temporary redirection"
        StatusCode.PERM_REDIRECTION.code -> "Permanent redirection"
        StatusCode.TEMP_FAIL.code -> "Temporary failure"
        StatusCode.TEMP_FAIL_SERVER.code -> "Server unavailable"
        StatusCode.TEMP_FAIL_CGI.code -> "CGI Error"
        StatusCode.TEMP_FAIL_PROXY.code -> "Proxy error"
        StatusCode.TEMP_FAIL_SLOW.code -> "Slow down"
        StatusCode.PERM_FAIL.code -> "Permanent failure"
        StatusCode.PERM_FAIL_NOT_FOUND.code -> "Not found"
        StatusCode.PERM_FAIL_GONE.code -> "Gone"
        StatusCode.PERM_FAIL_PROXY.code -> "Proxy request failed"
        StatusCode.PERM_FAIL_BAD.code -> "Bad request"
        StatusCode.CERT.code -> "Certificate required"
        StatusCode.CERT_NOT_AUTH.code -> "Certificate not authorized"
        StatusCode.CERT_NOT_VALID.code -> "Certificate not valid"

        else -> "Unknown"
    }

    fun description(code: Int) = when (code) {
        StatusCode.INPUT.code -> "The server requests user input."
        StatusCode.SENSITIVE_INPUT.code -> "The server requests sensitive input."
        StatusCode.SUCCESS.code -> "Page successfully loaded!"
        StatusCode.TEMP_REDIRECTION.code -> "You will be redirected to another address returned by the server. The server may disable redirection in the near future."
        StatusCode.PERM_REDIRECTION.code -> "You will be redirected to another address returned by the server. It's strongly advised to use the new address, as the original may get down at any time."
        StatusCode.TEMP_FAIL.code -> "This is a general error. Specific reason is unknown. Try again later."
        StatusCode.TEMP_FAIL_SERVER.code -> "The server isn't available now. Try again later."
        StatusCode.TEMP_FAIL_CGI.code -> "The server has some internal issues now. Try again later."
        StatusCode.TEMP_FAIL_PROXY.code -> "Proxy has some issues now. Try again later."
        StatusCode.TEMP_FAIL_SLOW.code -> "You've sent too much requests. Try again later."
        StatusCode.PERM_FAIL.code -> "This is a general error. Specific reason is unknown. It's unlikely that the page will load any time soon."
        StatusCode.PERM_FAIL_NOT_FOUND.code -> "Couldn't connect to the specified address."
        StatusCode.PERM_FAIL_GONE.code -> "The server won't serve any pages any time soon."
        StatusCode.PERM_FAIL_PROXY.code -> "Proxy request failed"
        StatusCode.PERM_FAIL_BAD.code -> "Request is malformed or doesn't satisfy server-side requirements."
        StatusCode.CERT.code -> ""
        StatusCode.CERT_NOT_AUTH.code -> ""
        StatusCode.CERT_NOT_VALID.code -> ""

        else -> ""
    }
}