package dev.sermah.geminibrowser.model

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
                        text { item.text }
                    }

                    is GemtextItem.Link -> tag("button") {
                        text { item.text }
                    }

                    is GemtextItem.List -> tag("ul") {
                        text { item.text }
                    }

                    is GemtextItem.Preformat -> tag("pre") {
                        text { item.text }
                    }

                    is GemtextItem.Heading -> tag(
                        when (item.level) {
                            GemtextItem.Heading.Level.H1 -> "h1"
                            GemtextItem.Heading.Level.H2 -> "h2"
                            GemtextItem.Heading.Level.H3 -> "h3"
                        }
                    ) {
                        text { item.text }
                    }

                    is GemtextItem.Quote -> tag("quote") {
                        text { item.text }
                    }

                    is GemtextItem.ParseError -> tag("div") {
                        classes("gemtext-error")
                        tag("p") {
                            text { "Error:" }
                            tag("br")
                            text { item.message }
                        }
                        tag("p") {
                            text { "Source line:" }
                            tag("br")
                            text { item.sourceLine }
                        }
                    }
                }
            }
        }.toString()
    }
}