package dev.sermah.geminibrowser.model

class GemtextParserImpl : GemtextParser {
    override fun parse(gemtext: String): GemtextParser.ParseResult {
        var state = ParsingState.NORMAL

        val lines = gemtext.lineIterator()
        val items = mutableListOf<GemtextParser.GemtextItem>()
        val preformatBuilder = StringBuilder()

        for (line in lines) {
            when (state) {
                ParsingState.NORMAL -> {
                    if (line.startsWith("```")) {
                        state = ParsingState.PREFORMATTED
                        preformatBuilder.clear()
                    } else if (line.startsWith("=>")) {
                        val content = line.drop(2).trim().split(
                            ' ', '\t',
                            ignoreCase = false,
                            limit = 2
                        )
                        items.add(
                            GemtextParser.GemtextItem.Link(
                                url = content[0].trim(),
                                text = if (content.size > 1) content[1].trim() else "",
                            )
                        )
                    } else if (line.startsWith("###")) {
                        items.add(
                            GemtextParser.GemtextItem.Heading(
                                GemtextParser.GemtextItem.Heading.Level.H3,
                                line.drop(3).trim(),
                            )
                        )
                    } else if (line.startsWith("##")) {
                        items.add(
                            GemtextParser.GemtextItem.Heading(
                                GemtextParser.GemtextItem.Heading.Level.H2,
                                line.drop(2).trim(),
                            )
                        )
                    } else if (line.startsWith("#")) {
                        items.add(
                            GemtextParser.GemtextItem.Heading(
                                GemtextParser.GemtextItem.Heading.Level.H1,
                                line.drop(1).trim(),
                            )
                        )
                    } else if (line.startsWith("* ")) {
                        items.add(
                            GemtextParser.GemtextItem.List(
                                line.drop(2).trim(),
                            )
                        )
                    } else if (line.startsWith("> ")) {
                        items.add(
                            GemtextParser.GemtextItem.Quote(
                                line.drop(2).trim(),
                            )
                        )
                    } else {
                        items.add(
                            GemtextParser.GemtextItem.Text(
                                line.trim(),
                            )
                        )
                    }
                }

                ParsingState.PREFORMATTED -> {
                    if (line.startsWith("```")) {
                        state = ParsingState.NORMAL
                        items.add(
                            GemtextParser.GemtextItem.Preformat(
                                preformatBuilder.toString(),
                            )
                        )
                    } else {
                        if (preformatBuilder.isNotEmpty())
                            preformatBuilder.appendLine()
                        preformatBuilder.append(line)
                    }
                }
            }
        }

        if (state == ParsingState.PREFORMATTED) {
            items.add(
                GemtextParser.GemtextItem.Preformat(
                    preformatBuilder.toString(),
                )
            )
        }

        return GemtextParser.ParseResult.from(items)
    }

    private fun String.lineIterator(): Iterator<String> = lineSequence().iterator()

    private enum class ParsingState {
        NORMAL, PREFORMATTED
    }
}