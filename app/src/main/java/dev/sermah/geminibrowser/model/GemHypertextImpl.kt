package dev.sermah.geminibrowser.model

import androidx.core.text.htmlEncode
import dev.sermah.geminibrowser.model.GemtextParser.GemtextItem
import dev.sermah.geminibrowser.xml.XMLElement
import dev.sermah.geminibrowser.xml.buildXml
import dev.sermah.geminibrowser.xml.classes
import dev.sermah.geminibrowser.xml.tag
import dev.sermah.geminibrowser.xml.text

class GemHypertextImpl(
    private val gemtextParser: GemtextParser,
    private val convertHashtags: Boolean,
) : GemHypertext {
    override fun convertToHypertext(gemtext: String): String {
        val parseResult = gemtextParser.parse(gemtext)

        return buildXml("html") {
            tag("head") {
                tag("style") {
                    text { DEFAULT_STYLE }
                }
                tag("script", "type" to "text/javascript") {
                    text { APP_INTERFACE_SCRIPT }
                }
            }
            tag("body") {
                parseResult.forEach { item ->
                    when (item) {
                        is GemtextItem.Text -> tag("p") {
                            text { item.text.htmlEncode() }
                        }

                        is GemtextItem.Link -> tag("p") {
                            val link = item.url.htmlEncode()
                            tag(
                                "a",
                                "onclick" to "onClickLink(event)",
                                "href" to link,
                            ) {
                                text {
                                    if (item.text.isNotBlank())
                                        item.text.htmlEncode()
                                    else
                                        link
                                }
                            }
                        }

                        is GemtextItem.List -> {
                            val li = buildXml("li") {
                                text { item.text.htmlEncode() }
                            }
                            val last: Any? = last()

                            if (last is XMLElement && last.tag == "ul") {
                                last.children.add(li)
                            } else {
                                tag("ul") {
                                    appendChild(li)
                                }
                            }
                        }

                        is GemtextItem.Preformat -> tag("pre") {
                            text { item.text.htmlEncode() }
                        }

                        is GemtextItem.Heading -> tag(
                            when (item.level) {
                                GemtextItem.Heading.Level.H1 -> "h1"
                                GemtextItem.Heading.Level.H2 -> "h2"
                                GemtextItem.Heading.Level.H3 -> "h3"
                            }
                        ) {
                            text { item.text.htmlEncode() }
                        }

                        is GemtextItem.Quote -> {
                            val p = buildXml("p") {
                                text { item.text.htmlEncode() }
                            }
                            val last: Any? = last()

                            if (last is XMLElement && last.tag == "blockquote") {
                                last.children.add(p)
                            } else {
                                tag("blockquote") {
                                    appendChild(p)
                                }
                            }
                        }

                        is GemtextItem.ParseError -> tag("div") {
                            classes("gemtext-error")
                            tag("p") {
                                text { "Error:" }
                                tag("br")
                                text { item.message.htmlEncode() }
                            }
                            tag("p") {
                                text { "Source line:" }
                                tag("br")
                                text { item.sourceLine.htmlEncode() }
                            }
                        }
                    }
                }
            }
        }.toString().let {
            if (convertHashtags) it.replace("#", "%23")
            else it
        }
    }

    private companion object {
        val APP_INTERFACE_SCRIPT =
            """
                function onClickLink(e) {
                    e.preventDefault();
                    var link = e.target.getAttribute("href");
                    window.appInterface.onClickLink(link);
                }
            """.trimIndent()
        val DEFAULT_STYLE =
            """
                body {
                    margin: 32pt 16pt;
                }
                a:before {
                    content: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPScyNCcgaGVpZ2h0PScxNicgdmlld0JveD0nMCAwIDI0IDE2Jz48cGF0aCBkPSdNNCA2aDEyTTQgMTBoMTJtLTQtOCA2IDYtNiA2JyBzdHJva2U9JyM1Mjc5NmYnIGZpbGw9J25vbmUnLz48L3N2Zz4=");
                    width: 12pt;
                    height: 8pt;
                    padding-right: 8pt;
                    transform: translateY(2pt);
                    display: inline-block;
                }
                a {
                    color: #52796f;
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
}