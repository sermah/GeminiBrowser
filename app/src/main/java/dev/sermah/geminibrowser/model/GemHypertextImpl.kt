package dev.sermah.geminibrowser.model

import androidx.core.text.htmlEncode
import dev.sermah.geminibrowser.model.GemtextParser.GemtextItem
import dev.sermah.geminibrowser.xml.XMLElement
import dev.sermah.geminibrowser.xml.attributes
import dev.sermah.geminibrowser.xml.buildXml
import dev.sermah.geminibrowser.xml.classes
import dev.sermah.geminibrowser.xml.tag
import dev.sermah.geminibrowser.xml.text

class GemHypertextImpl(
    private val gemtextParser: GemtextParser
) : GemHypertext {
    override fun convertToHypertext(gemtext: String): String {
        val parseResult = gemtextParser.parse(gemtext)
        return buildXml("html") {
            tag("script") {
                attributes("type" to "text/javascript")
                text {
                    """
                        function onClickLink(link) {
                            window.appInterface.onClickLink(link);
                        }
                    """.trimIndent()
                }
            }
            parseResult.forEach { item ->
                when (item) {
                    is GemtextItem.Text -> tag("p") {
                        text { item.text.htmlEncode() }
                    }

                    is GemtextItem.Link -> tag("p") {
                        val link = item.url.htmlEncode()
                        tag("button") {
                            attributes("onclick" to "onClickLink('$link')")
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
        }.toString()
    }
}