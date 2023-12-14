package com.example.androidtwoctwopsandbox

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PromptPayActivity : AppCompatActivity() {

    private var imageUrl: String? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt_pay)


        val webView: WebView = findViewById(R.id.image_web_view)
        webView.settings.setJavaScriptEnabled(true)
        webView.settings.setUseWideViewPort(true);
        webView.settings.setLoadWithOverviewMode(true);

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }

                return true
            }
        }

        val imageUrl = intent.getStringExtra("image_url")

        if (imageUrl != null) {
            println("imageUrl: $imageUrl")
            this.imageUrl = imageUrl
//            webView.loadUrl("https://www.google.com")
            webView.loadDataWithBaseURL(null,
                "<html><head></head><body><img src=\"$imageUrl\" /></body></html>", "text/html", "UTF-8", null);
        }
    }

    fun passImageAsResult() {
        intent.putExtra("image_url", this.imageUrl);

        setResult(Activity.RESULT_OK, intent);
        finish()
    }
    override fun onDestroy () {
        super.onDestroy()

    }
}