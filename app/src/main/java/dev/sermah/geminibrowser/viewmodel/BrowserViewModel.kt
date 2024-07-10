package dev.sermah.geminibrowser.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.sermah.geminibrowser.model.GemHypertextImpl
import dev.sermah.geminibrowser.model.GemtextParserImpl
import kotlinx.coroutines.flow.MutableStateFlow

class BrowserViewModel : ViewModel() {
    private val _htmlFlow = MutableStateFlow(TEST_GEM_PARSED)
    val htmlFlow get() = _htmlFlow

    fun openUrl(url: String) {
        Log.d(TAG, "openUrl($url)")
    }

    companion object {
        const val TAG = "BrowserViewModel"

        val TEST_GEM_PARSED = GemHypertextImpl(GemtextParserImpl()).convertToHypertext(
            """
                    ## This is a Gemtext Document
                    
                    Actual text
                    Line 2
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vitae imperdiet lorem, ut scelerisque mi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Quisque urna sem, finibus in dui sed, rutrum facilisis odio. Nunc vitae urna pellentesque turpis blandit finibus eget ac nibh. Curabitur tempus ipsum a tincidunt ullamcorper. Integer massa nunc, facilisis a feugiat sit amet, dictum non odio. Aliquam id ipsum purus. Morbi aliquet sapien eu dolor auctor vulputate. Morbi ultrices molestie enim, vel placerat lectus consectetur ut. Sed velit enim, pretium eget orci quis, auctor convallis sem. Sed non tortor ante.
                    
                    Integer imperdiet enim enim, vitae semper libero commodo eget. Quisque non lorem tristique, faucibus magna at, interdum mauris. Nam porta neque non dignissim porta. Duis ac tellus non urna sagittis molestie ac at dolor. Interdum et malesuada fames ac ante ipsum primis in faucibus. Praesent porttitor, turpis vitae pellentesque facilisis, risus risus mollis nisl, at placerat ex magna blandit nibh. Sed nec ante est.

                    * This is a simple bullet point list.
                    * Nested bullet points are indented with a space.
                    
                    > They can be nested multiple levels.
                    > This is a numbered list.
                    
                    # This is a heading.
                    ## This is a subheading.
                    ### This is a subsubheading.
                    
                    ```
                    This is some code.
                    It can be multiple lines long.
                    ```
                    
                    => hrrps://utl.s.a
                    => https://ln.kii Link button
                    
                    <b> html tags </b>
                """.trimIndent()
        )
    }
}