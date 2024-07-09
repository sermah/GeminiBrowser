package dev.sermah.geminibrowser.model

interface GemtextParser {

    fun parse(gemtext: String): ParseResult

    class ParseResult private constructor(
        private val items: List<GemtextItem>
    ) : Collection<GemtextItem> by items {

        companion object {
            fun from(list: List<GemtextItem>): ParseResult {
                return ParseResult(list)
            }
        }
    }

    sealed class GemtextItem {
        data class Text(
            val text: String,
        ) : GemtextItem()

        data class Link(
            val url: String,
            val text: String,
        ) : GemtextItem()

        data class Preformat(
            val text: String,
        ) : GemtextItem()

        data class Heading(
            val level: Level,
            val text: String,
        ) : GemtextItem() {
            enum class Level {
                H1, H2, H3
            }
        }

        data class List(
            val text: String,
        ) : GemtextItem()

        data class Quote(
            val text: String,
        ) : GemtextItem()

        data class ParseError(
            val sourceLine: String,
            val message: String,
        ) : GemtextItem()
    }
}