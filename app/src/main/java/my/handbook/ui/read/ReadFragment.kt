package my.handbook.ui.read

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import my.handbook.R

class ReadFragment : Fragment() {

    private val args: ReadFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_read, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.web_view)
        webView.settings.apply {
            defaultFontSize = 16
        }
        webView.loadUrl("file:///android_asset/${args.fileName}")

        var text = args.searchResult
        if (text.isNotEmpty()) {
            text = text.replace("<b>","")
            text = text.replace("</b>","")
            Toast.makeText(requireContext(), "findAllAsync: $text", Toast.LENGTH_SHORT).show()
            webView.findAllAsync(text)
        }
    }
}
