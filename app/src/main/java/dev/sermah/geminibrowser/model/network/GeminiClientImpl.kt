package dev.sermah.geminibrowser.model.network

import android.net.Uri
import android.util.Log
import dev.sermah.geminibrowser.AppDispatchers
import dev.sermah.geminibrowser.model.network.GeminiClient.GeminiResponse
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.SocketFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class GeminiClientImpl : GeminiClient {

    // Create SocketFactory that allows self-signed certificates (disables certificate validation O_o)
    // Still, having this instead of non-ssl connection is better for future
    // TODO Change da world
    private val socketFactory: SocketFactory by lazy {
        val context: SSLContext = SSLContext.getInstance("TLS")
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<out X509Certificate>? {
                return arrayOf()
            }
        })
        context.init(null, trustAllCerts, null)

        context.socketFactory
    }

    override suspend fun get(url: String): GeminiResponse = runCatching {
        withContext(AppDispatchers.IO) {
            val parsedUrl = Uri.parse(url)

            checkUri(parsedUrl)

            val port = parsedUrl.port.let { if (it == -1) 1965 else it }
            val request = "$parsedUrl\r\n"

            var header: String
            val bodyBuilder = StringBuilder()

            socketFactory.createSocket(parsedUrl.host, port).use { sock ->
                sock.soTimeout = 10_000
                Log.d(TAG, "Connected to (${sock.inetAddress}, ${sock.port}) = ${sock.isConnected}")

                val input = sock.inputStream.bufferedReader()
                val output = sock.outputStream.writer()

                Log.d(TAG, "Sending req '${request.trimEnd()}'")

                output.write(request)
                output.flush()

                header = input.readLine()
                Log.d(TAG, "Read header $header")

                if (header.length >= 2 && header[0] == '2')
                    input.readLines().forEach {
                        bodyBuilder.appendLine(it)
                    }
            }

            if (header.length < 2)
                throw GeminiClient.IncorrectHeaderException("Header length must be 2 or greater. Actual length is ${header.length}")

            val code: Int = (header[0] - '0') * 10 + (header[1] - '0')

            if (code !in 10..69)
                throw GeminiClient.IncorrectHeaderException("Status code ($code) should be in 10..69")

            val headerMsg = if (header.length > 3) header.substring(3) else ""
            val body = bodyBuilder.toString()

            Log.d(TAG, "code $code, headerMsg $headerMsg, body.len ${body.length}")

            return@withContext when (code) {
                in 10..19 -> GeminiResponse.Input(code, headerMsg)
                in 20..29 -> GeminiResponse.Success(code, headerMsg, body)
                in 30..39 -> GeminiResponse.Redirect(code, headerMsg)
                in 40..49 -> GeminiResponse.TempFail(code, headerMsg)
                in 50..59 -> GeminiResponse.PermFail(code, headerMsg)
                in 60..69 -> GeminiResponse.Auth(code, headerMsg)
                else -> throw IllegalArgumentException("Unknown code $code")
            }
        }
    }.onFailure { ex ->
        when (ex) {
            is UnknownHostException,
            is NoRouteToHostException,
            ->
                throw GeminiClient.ConnectionFailedException(ex)

            is IOException ->
                throw GeminiClient.ConnectionException(ex)
        }
    }.getOrThrow()

    /**
     * Checks that the given [url] is a valid gemini url
     *
     * @throws GeminiClient.WrongProtocolException if the scheme is not gemini
     * @throws GeminiClient.MalformedUriException if the uri is malformed or null
     */
    private fun checkUri(url: Uri?) {
        when {
            url == null ->
                throw GeminiClient.MalformedUriException("Uri is null, thus wasn't parsed correctly")

            url.scheme != "gemini" ->
                throw GeminiClient.WrongProtocolException("Uri scheme is not gemini (\"${url.scheme}\")")

            url.host == null ->
                throw GeminiClient.MalformedUriException("Uri host is null")
        }
    }

    private companion object {
        const val TAG = "GeminiClientImpl"
    }
}