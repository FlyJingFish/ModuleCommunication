package com.flyjingfish.modulecommunication

import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import com.flyjingfish.modulecommunication.databinding.ActivityWebBinding

class WebActivity : ComponentActivity() {
    private lateinit var binding : ActivityWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.web.destroy()
    }

    override fun onResume() {
        super.onResume()
        binding.web.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.web.onPause()
    }

    fun initView() {
        setWebHtmlView()
        binding.web.webViewClient = CharmWebViewClient()
        binding.web.webChromeClient = CharmWebChromeClient()
        //        binding.mlStatus.showLoading();
        binding.web.loadUrl("https://flyjingfish.github.io/demoweb/indexUri.html")
    }
    private fun setWebHtmlView() {
        val s: WebSettings = binding.web.settings
        s.allowFileAccess = false
        s.allowFileAccessFromFileURLs = false
        s.saveFormData = false
        s.savePassword = false
        s.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        s.javaScriptEnabled = true
        s.cacheMode = WebSettings.LOAD_DEFAULT
        s.domStorageEnabled = true
        s.javaScriptCanOpenWindowsAutomatically = true
        s.setSupportZoom(false)
        s.builtInZoomControls = false
        s.displayZoomControls = false
        s.useWideViewPort = true
        s.loadWithOverviewMode = true
//        binding.web.addJavascriptInterface(this, "nativeApi")
        s.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }

    private inner class CharmWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return if (url.startsWith("http") || url.startsWith("https")) {
                view.loadUrl(url)
                true
            } else {
                try {
                    startActivity(Intent.parseUri(url, 0))
                    true
                } catch (e: Exception) {
                    true
                }
            }
        }
    }

    private inner class CharmWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
        }
    }
}