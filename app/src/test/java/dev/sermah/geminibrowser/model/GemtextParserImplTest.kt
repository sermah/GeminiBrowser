package dev.sermah.geminibrowser.model

import dev.sermah.geminibrowser.model.GemtextParser.*
import org.junit.Assert.*

import org.junit.Test

class GemtextParserImplTest {

    private val parser = GemtextParserImpl()

    @Test
    fun `parse links`() = parseAndAssert(
        """
            => gemini://example.org/
            => gemini://example.org/ An example link
            => gemini://example.org/foo	Another example link at the same host
            => foo/bar/baz.txt	A relative link
            => 	gopher://example.org:70/1 A gopher link
        """.trimIndent(),
        listOf(
            GemtextItem.Link("gemini://example.org/", ""),
            GemtextItem.Link("gemini://example.org/", "An example link"),
            GemtextItem.Link("gemini://example.org/foo", "Another example link at the same host"),
            GemtextItem.Link("foo/bar/baz.txt", "A relative link"),
            GemtextItem.Link("gopher://example.org:70/1", "A gopher link")
        )
    )

    @Test
    fun `parse headings`() = parseAndAssert(
        """
            # This is a heading
            ## This is a subheading
            ### This is a subsubheading
            #      This is a heading 1
            ##This is a subheading 2
            ###     This is a subsubheading 3
            #
            ##     
            ###     
        """.trimIndent(),
        listOf(
            GemtextItem.Heading(GemtextItem.Heading.Level.H1, "This is a heading"),
            GemtextItem.Heading(GemtextItem.Heading.Level.H2, "This is a subheading"),
            GemtextItem.Heading(GemtextItem.Heading.Level.H3, "This is a subsubheading"),
            GemtextItem.Heading(GemtextItem.Heading.Level.H1, "This is a heading 1"),
            GemtextItem.Heading(GemtextItem.Heading.Level.H2, "This is a subheading 2"),
            GemtextItem.Heading(GemtextItem.Heading.Level.H3, "This is a subsubheading 3"),
            GemtextItem.Heading(GemtextItem.Heading.Level.H1, ""),
            GemtextItem.Heading(GemtextItem.Heading.Level.H2, ""),
            GemtextItem.Heading(GemtextItem.Heading.Level.H3, ""),
        )
    )

    @Test
    fun `parse quotes`() = parseAndAssert(
        """
            > This is a quote
            > This is another quote
            >   This is spaced quote
        """.trimIndent(),
        listOf(
            GemtextItem.Quote("This is a quote"),
            GemtextItem.Quote("This is another quote"),
            GemtextItem.Quote("This is spaced quote")
        )
    )

    @Test
    fun `parse lists`() = parseAndAssert(
        """
            * This is a list
            * This is another list
            *   This is spaced list
        """.trimIndent(),
        listOf(
            GemtextItem.List("This is a list"),
            GemtextItem.List("This is another list"),
            GemtextItem.List("This is spaced list")
        )
    )

    @Test
    fun `parse complex 1`() = parseAndAssert(
        """
            # Good morning
            
            This is a text.
            
            * This is a list
            * With another point
            
            > Then some quote by someone
            > And another quote
            
            => https://test.example Also links
            => gemini://forgor.example
        """.trimIndent(),
        listOf(
            GemtextItem.Heading(GemtextItem.Heading.Level.H1, "Good morning"),
            GemtextItem.Text(""),
            GemtextItem.Text("This is a text."),
            GemtextItem.Text(""),
            GemtextItem.List("This is a list"),
            GemtextItem.List("With another point"),
            GemtextItem.Text(""),
            GemtextItem.Quote("Then some quote by someone"),
            GemtextItem.Quote("And another quote"),
            GemtextItem.Text(""),
            GemtextItem.Link("https://test.example", "Also links"),
            GemtextItem.Link("gemini://forgor.example", "")
        )
    )

    @Test
    fun `parse complex 2`() = parseAndAssert(
        """
            ### Good morning
            
            Preformatting test
            
            ```
            Hello, some usual line
            
               Spaced before
            
            Spaced after     
                 
            ```
            
            Preformat end
        """.trimIndent(),
        listOf(
            GemtextItem.Heading(GemtextItem.Heading.Level.H3, "Good morning"),
            GemtextItem.Text(""),
            GemtextItem.Text("Preformatting test"),
            GemtextItem.Text(""),
            GemtextItem.Preformat("""
                Hello, some usual line
                
                   Spaced before
                
                Spaced after     
                     
            """.trimIndent()),
            GemtextItem.Text(""),
            GemtextItem.Text("Preformat end"),
        )
    )

    private fun parseAndAssert(gemtext: String, expected: List<GemtextItem>) {
        val result = parser.parse(gemtext)
        assertParseResult(result, expected)
    }

    private fun assertParseResult(result: ParseResult, expected: List<GemtextItem>) {
        assertEquals(expected, result.toList())
    }
}