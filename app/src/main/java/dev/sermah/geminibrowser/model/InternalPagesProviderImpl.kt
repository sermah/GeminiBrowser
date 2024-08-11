package dev.sermah.geminibrowser.model

import dev.sermah.geminibrowser.texts.ErrorTexts
import dev.sermah.geminibrowser.texts.StatusCodeTexts
import dev.sermah.geminibrowser.xml.buildXml
import dev.sermah.geminibrowser.xml.tag
import dev.sermah.geminibrowser.xml.text

class InternalPagesProviderImpl(val convertHashtags: Boolean) : InternalPagesProvider {
    override fun getErrorPage(code: Int, data: Map<String, String>): String {
        val title = StatusCodeTexts.title(code)
        val description = StatusCodeTexts.description(code)
        return buildXml("html") {
            tag("head") {
                tag("style") {
                    text { ERROR_STYLE }
                }
            }
            tag("body") {
                tag("h1") { text { title } }
                tag("p") { text { description } }
                tag("pre") { text { "Status code: $code" } }
            }
        }.toString().maybeConvertHashtags()
    }

    override fun getErrorPage(throwable: Throwable, data: Map<String, String>): String {
        val title = ErrorTexts.title(throwable)
        val description = ErrorTexts.description(throwable)
        return buildXml("html") {
            tag("head") {
                tag("style") {
                    text { ERROR_STYLE }
                }
            }
            tag("body") {
                tag("h1") { text { title } }
                tag("p") { text { description } }
                tag("pre") { text { "Error - ${throwable.javaClass.simpleName}:\n${throwable.message}" } }
            }
        }.toString().let {
            if (convertHashtags) it.replace("#", "%23")
            else it
        }.maybeConvertHashtags()
    }

    private companion object {
        val ERROR_STYLE =
            """
                body {
                    margin: 32pt 16pt;
                }
                pre {
                    background-color: #eeeeee;
                    color: #333333;
                    overflow: auto;
                    padding: 16pt 8pt;
                    border-radius: 8pt;
                }
                blockquote {
                    background-color: #e4f0d0;
                    color: #2f3e46;
                    border-left: solid 2pt #2f3e46;
                    margin: 0;
                    padding: 16pt 8pt;
                }
                blockquote p {
                    margin: 0;
                }
                ul {
                    padding-left: 16pt;
                }
            """.trimIndent()
    }

    private fun String.maybeConvertHashtags(): String =
        if (convertHashtags) this.replace("#", "%23") else this
}