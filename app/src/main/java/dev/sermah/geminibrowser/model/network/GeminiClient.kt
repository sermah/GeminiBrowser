package dev.sermah.geminibrowser.model.network

interface GeminiClient {
    suspend fun get(url: String): GeminiResponse

    sealed class GeminiResponse(val code: Int) {
        // TODO Add support for inputs
        class Input(
            code: Int,
            val prompt: String,
        ) : GeminiResponse(code)

        class Success(
            code: Int,
            val mimeType: String,
            val body: String,
        ) : GeminiResponse(code)

        // TODO Add support for redirects
        class Redirect(
            code: Int,
            val uriReference: String,
        ) : GeminiResponse(code)

        // TODO Add support for failures
        class TempFail(
            code: Int,
            val errorMessage: String,
        ) : GeminiResponse(code)

        class PermFail(
            code: Int,
            val errorMessage: String,
        ) : GeminiResponse(code)

        // TODO Add support for certificates
        class Auth(
            code: Int,
            val errorMessage: String,
        ) : GeminiResponse(code)

        enum class StatusCode(val code: Int) {
            INPUT(10),
            SENSITIVE_INPUT(11),

            SUCCESS(20),

            TEMP_REDIRECTION(30), // temporary redirection
            PERM_REDIRECTION(31), // permanent redirection

            TEMP_FAIL(40),
            TEMP_FAIL_SERVER(41), // server unavailable
            TEMP_FAIL_CGI(42), // CGI error
            TEMP_FAIL_PROXY(43), // proxy error
            TEMP_FAIL_SLOW(43), // slow down

            PERM_FAIL(50),
            PERM_FAIL_NOT_FOUND(51), // not found
            PERM_FAIL_GONE(52), // gone
            PERM_FAIL_PROXY(53), // proxy request failed
            PERM_FAIL_BAD(59), // bad request

            CERT(60), // requires cert
            CERT_NOT_AUTH(61), // certificate not authorized
            CERT_NOT_VALID(62) // certificate not valid
        }
    }

    // TODO Use connection exceptions in GeminiClientImpl
    class WrongProtocolException(msg: String) : Exception(msg)
    class MalformedUriException(msg: String) : Exception(msg)
    class ConnectionDisruptedException(msg: String) : Exception(msg)
    class ConnectionFailedException(msg: String) : Exception(msg)
    class WrongHeaderException(msg: String) : Exception(msg)
}