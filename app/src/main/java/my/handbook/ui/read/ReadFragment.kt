package my.handbook.ui.read

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.R
import my.handbook.common.isDarkThemeEnabled
import my.handbook.databinding.FragmentReadBinding
import my.handbook.ui.base.BaseFragment

@AndroidEntryPoint
class ReadFragment : BaseFragment<FragmentReadBinding>() {

    override val layoutRes: Int = R.layout.fragment_read
    override val viewModel: ReadViewModel by viewModels()

    override fun initViews() {
        binding.readWebView.apply {
            if (context.isDarkThemeEnabled() && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    viewModel.onPageStarted()
                }
                override fun onPageFinished(view: WebView, url: String) {
                    viewModel.onPageFinished()
                }
            }
        }
    }

    override fun observeData() {
        viewModel.url.observe(viewLifecycleOwner) {
            binding.readWebView.loadUrl(it)
        }
        viewModel.searchText.observe(viewLifecycleOwner) {
            binding.readWebView.findAllAsync(it)
        }
    }
}
