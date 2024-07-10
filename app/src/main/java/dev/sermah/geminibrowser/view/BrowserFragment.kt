package dev.sermah.geminibrowser.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.sermah.geminibrowser.databinding.FragmentBrowserBinding
import dev.sermah.geminibrowser.model.GemHypertextImpl
import dev.sermah.geminibrowser.model.GemtextParserImpl


class BrowserFragment: Fragment() {

    private lateinit var binding: FragmentBrowserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBrowserBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val data = GemHypertextImpl(GemtextParserImpl()).convertToHypertext(
            """
                    ## This is a Gemtext Document

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
        binding.webView.apply {
            settings.javaScriptEnabled = true
            loadData(data, "text/html; charset=utf-8", "UTF-8")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}