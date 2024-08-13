package dev.sermah.geminibrowser.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.sermah.geminibrowser.R
import dev.sermah.geminibrowser.databinding.FragmentBrowserBinding
import dev.sermah.geminibrowser.model.TabBrowser
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

        binding.apply {
            btnAddrRefresh.setOnClickListener { onRefreshClicked() }
            btnNavBack.setOnClickListener { onBackClicked() }
            btnNavForward.setOnClickListener { onForwardClicked() }
        }

        viewModel.pageFlow.onEach(::onPage).launchIn(viewModel.viewModelScope)
        viewModel.tabStateFlow.onEach(::onState).launchIn(viewModel.viewModelScope)
    }

    override fun onDestroyView() {
        binding.webView.destroy()
        _binding = null

        super.onDestroyView()
    }

    private fun onPage(page: TabBrowser.Page) {
        binding.tvAddrText.text = page.url

        // Did you yourHtml.replace("#", "%23") before panicking?
        binding.webView.loadData(page.html, "text/html; charset=utf-8", "utf-8")
    }

    private fun onState(state: TabBrowser.State) {
        binding.btnNavBack.apply {
            isEnabled = state.canGoBack
            alpha = if (isEnabled) 1f else 0.5f
        }

        binding.btnNavForward.apply {
            isEnabled = state.canGoForward
            alpha = if (isEnabled) 1f else 0.5f
        }

        binding.btnAddrRefresh.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                if (state.isLoading) {
                    R.drawable.ic_cross
                } else {
                    R.drawable.ic_refresh
                }
            )
        )
    }

    private fun onRefreshClicked() {
        if (viewModel.tabStateFlow.value.isLoading) {
            viewModel.stop()
        } else {
            viewModel.refresh()
        }
    }

    private fun onBackClicked() {
        viewModel.back()
    }

    private fun onForwardClicked() {
        viewModel.forward()
    }

    @JavascriptInterface
    fun onClickLink(link: String?) {
        link?.let { viewModel.openUrl(it) }
    }
}
