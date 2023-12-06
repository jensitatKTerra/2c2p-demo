package com.example.androidtwoctwopsandbox

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.ccpp.pgw.sdk.android.builder.PGWSDKParamsBuilder
import com.ccpp.pgw.sdk.android.core.PGWSDK
import com.ccpp.pgw.sdk.android.enums.APIEnvironment
import com.ccpp.pgw.sdk.android.model.core.PGWSDKParams
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

data class RecargeRespData(
    val webPaymentUrl: String,
    val paymentToken: String
)

data class RecargeResp(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: RecargeRespData
)

class MainActivity : AppCompatActivity() {

    fun makeRequest() {
        val okHttpClient = OkHttpClient()
        val parsedResponse = parseResponse(okHttpClient.newCall(createRequest()).execute())

        val result = Klaxon()
            .parse<RecargeResp>(parsedResponse)

        if (result?.data != null) {
            val webPaymentUrl = result.data.webPaymentUrl

            val pgwsdkParams: PGWSDKParams = PGWSDKParamsBuilder(this, APIEnvironment.Production)
                .build()

            PGWSDK.initialize(pgwsdkParams)

            val fragment = PGWWebViewFragment.newInstance(webPaymentUrl)
            this@MainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.layout_container, fragment, "fragmnet1")
                .commit();
        }
    }

    fun createRequest(): Request {

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("pgGateway", "2c2p")
            .addFormDataPart("amount", "100")
            .build()

        return Request.Builder()
            .url("https://humane-vocal-tuna.ngrok-free.app/api/v1/user/wallet/recharge/")
            .post(requestBody)
            .build()
    }

    fun parseResponse(response: Response): String {
        val body = response.body?.string() ?: ""

        return body
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // get reference to button
        val payment_button = findViewById(R.id.payment_button) as Button
        // set on-click listener
        payment_button.setOnClickListener {

            makeRequest()



            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        }


    }
}