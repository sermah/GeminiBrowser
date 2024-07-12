package dev.sermah.geminibrowser.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.sermah.geminibrowser.databinding.FragmentBrowserBinding
import dev.sermah.geminibrowser.viewmodel.BrowserViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BrowserFragment : Fragment() {

    private var _binding: FragmentBrowserBinding? = null
    private val binding: FragmentBrowserBinding
        get() = checkNotNull(_binding)

    private val viewModel: BrowserViewModel by lazy {
        ViewModelProvider(requireActivity())[BrowserViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBrowserBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(this@BrowserFragment, "appInterface")
        }

        viewModel.htmlFlow.onEach { html ->
            binding.webView.loadData(html, "text/html; charset=utf-8", "utf-8")
        }.launchIn(viewModel.viewModelScope)

        viewModel.openUrl("gemini://gemini.circumlunar.space/")
    }

    override fun onDestroyView() {
        binding.webView.destroy()
        _binding = null

        super.onDestroyView()
    }

    @JavascriptInterface
    fun onClickLink(link: String?) {
        link?.let { viewModel.openUrl(it) }
    }
}
