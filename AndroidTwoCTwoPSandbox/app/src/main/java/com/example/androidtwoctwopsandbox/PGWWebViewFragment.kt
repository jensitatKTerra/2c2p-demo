package com.example.androidtwoctwopsandbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.ccpp.pgw.sdk.android.callback.PGWWebViewClientCallback
import com.ccpp.pgw.sdk.android.callback.PGWWebViewTransactionStatusCallback
import com.ccpp.pgw.sdk.android.core.authenticate.PGWWebViewClient


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_REDIRECT_URL = "redirectUrl"

/**
 * A simple [Fragment] subclass.
 * Use the [PGWWebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PGWWebViewFragment : Fragment() {
    private val TAG = PGWWebViewFragment::class.java.name

    private var mRedirectUrl: String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.arguments != null) {

            mRedirectUrl = arguments?.getString(ARG_REDIRECT_URL);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Step 5: Authentication handling for 3DS payment.
        //Step 5: Authentication handling for 3DS payment.
        val webview = WebView(requireActivity())

        //Optional

        //Optional
        webview.settings.builtInZoomControls = true
        webview.settings.setSupportZoom(true)
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        //Mandatory

        //Mandatory
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true //Some bank page required. eg: am bank

        webview.webViewClient = PGWWebViewClient(mTransactionStatusCallback, mWebViewClientCallback)

        webview.loadUrl(mRedirectUrl!!)

        return webview
    }

    /**
     * Reference : https://developer.2c2p.com/v4.0.2/docs/api-sdk-transaction-status-inquiry
     */
    private val mTransactionStatusCallback = PGWWebViewTransactionStatusCallback {
        //Do Transaction Status Inquiry API and close this WebView.
    }

    private val mWebViewClientCallback: PGWWebViewClientCallback =
        object : PGWWebViewClientCallback {
            override fun shouldOverrideUrlLoading(url: String) {
                Log.i(TAG, "PGWWebViewClientCallback shouldOverrideUrlLoading : $url")
            }

            override fun onPageStarted(url: String) {
                Log.i(TAG, "PGWWebViewClientCallback onPageStarted : $url")
            }

            override fun onPageFinished(url: String) {
                Log.i(TAG, "PGWWebViewClientCallback onPageFinished : $url")
            }
        }

    companion object {

        @JvmStatic fun newInstance(redirectUrl: String) = PGWWebViewFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_REDIRECT_URL, redirectUrl)
            }
        }
    }
}