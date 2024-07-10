package dev.sermah.geminibrowser.model

import androidx.core.text.htmlEncode
import dev.sermah.geminibrowser.model.GemtextParser.GemtextItem
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
            parseResult.forEach { item ->
                when (item) {
                    is GemtextItem.Text -> tag("p") {
                        text { item.text.htmlEncode() }
                    }

                    is GemtextItem.Link -> tag("button") {
                        text { item.text.htmlEncode() }
                    }

                    is GemtextItem.List -> tag("li") {
                        text { item.text.htmlEncode() }
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

                    is GemtextItem.Quote -> tag("blockquote") {
                        text { item.text.htmlEncode() }
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