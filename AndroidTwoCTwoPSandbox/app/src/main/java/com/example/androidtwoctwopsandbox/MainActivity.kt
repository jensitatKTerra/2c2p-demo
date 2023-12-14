package com.example.androidtwoctwopsandbox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.models.Capability
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.Token
import co.omise.android.ui.AuthorizingPaymentActivity
import co.omise.android.ui.OmiseActivity
import co.omise.android.ui.PaymentCreatorActivity
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

data class OmiseRechargeCardRespData(
    val charge_id: String,
    val status: String,
    val authorize_uri: String,
)

data class OmiseRechargeCardResp(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: OmiseRechargeCardRespData
)

data class OmiseRechargeQrRespData(
    val charge_id: String,
    val status: String,
    val qr_image: String,
)

data class OmiseRechargeQrResp(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: OmiseRechargeQrRespData
)



class MainActivity : AppCompatActivity() {

    private val OMISE_PKEY: String = "pkey_test_5y12fgo4om2vr167qh7"

    fun makeRequest(paymentChannel: String) {
        val okHttpClient = OkHttpClient()
        val parsedResponse = parseResponse(okHttpClient.newCall(createRequest(paymentChannel)).execute())

        val result = Klaxon()
            .parse<RecargeResp>(parsedResponse)

        if (result?.data != null) {
            val webPaymentUrl = result.data.webPaymentUrl

            val pgwsdkParams: PGWSDKParams = PGWSDKParamsBuilder(this, APIEnvironment.Production)
                .build()

            PGWSDK.initialize(pgwsdkParams)

            val fragment = PGWWebViewFragment.newInstance(webPaymentUrl)
            this@MainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.layout_container, fragment, "fragment1")
                .commit();
        }
    }

    fun createRequest(paymentChannel: String): Request {

        val amount_text = findViewById(R.id.editTextNumber) as EditText

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("pgGateway", "2c2p")
            .addFormDataPart("amount", amount_text.text.toString())
            .addFormDataPart("paymentChannel", paymentChannel)
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

    }

    private fun createRechargeRequest(type: String = "card", value: String): Request {
        val amountText = findViewById<EditText>(R.id.editTextNumber)

        val requestBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("pgGateway", "Omise")
            .addFormDataPart("amount", amountText.text.toString())
        // QR
        if (type == "source") {
            requestBodyBuilder.addFormDataPart("source", value)
        // Card
        } else {
            requestBodyBuilder.addFormDataPart("card", value)
        }

        val requestBody: RequestBody = requestBodyBuilder.build()
        return Request.Builder()
            .url("https://humane-vocal-tuna.ngrok-free.app/api/v1/user/wallet/recharge/")
            .post(requestBody)
            .build()
    }

    private val capability = Capability.create(
        allowCreditCard = true,
        sourceTypes = listOf(SourceType.PromptPay) // QR
    )

    private fun showQRImageActivity(imageUrl: String) {
        val intent = Intent(applicationContext, PromptPayActivity::class.java)
        intent.putExtra("image_url", imageUrl)

        getPromptPayResult.launch(intent)
    }
    private fun showPaymentCreatorActivity() {
        val amount_text = findViewById(R.id.editTextNumber) as EditText
        val intent = Intent(applicationContext, PaymentCreatorActivity::class.java)
        intent.putExtra(OmiseActivity.EXTRA_PKEY, OMISE_PKEY)
        intent.putExtra(OmiseActivity.EXTRA_AMOUNT, amount_text.text.toString().toLong())
        intent.putExtra(OmiseActivity.EXTRA_CURRENCY, "thb")

        // you can retrieve your account's capabilities through the SDK (will be explained below)
        intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)

        getPaymentCreatorResult.launch(intent)
    }

    private fun showAuthorizingPaymentForm(authorizedUri: String) {
        val intent = Intent(applicationContext, AuthorizingPaymentActivity::class.java)
        intent.putExtra(AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING, authorizedUri)
        intent.putExtra(AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS,
            arrayOf("https://return.omise.terracharge.net")
        )
        getAuthorizingPaymentResult.launch(intent)
    }


    private val getPromptPayResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == RESULT_CANCELED) {
                // handle the cancellation
                return@registerForActivityResult
            }
            if (it.resultCode == RESULT_OK) {
//                val imageUrl = it.data?.getStringExtra("image_url")
//                println("imageUrl: $imageUrl")
                // Update Wallet
                return@registerForActivityResult
            }
        }

    private val getAuthorizingPaymentResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_CANCELED) {
                // handle the cancellation
                return@registerForActivityResult
            }
            if (it.resultCode == RESULT_OK) {
                val url = it.data?.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING)
                println("url: $url")
                // Update Wallet
                return@registerForActivityResult
            }
        }


    private val getPaymentCreatorResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            println("resultCode" + it.resultCode)
            if (it.resultCode == RESULT_CANCELED) {
                // handle the cancellation
                return@registerForActivityResult
            }
            if (it.resultCode == Activity.RESULT_OK) {

                if (it.data?.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT) == true) {
                    val source = it.data?.getParcelableExtra(OmiseActivity.EXTRA_SOURCE_OBJECT, Source::class.java)
                    println("source: " + source?.toString())

                    if (source?.id != null) {
                        val okHttpClient = OkHttpClient()
                        val parsedResponse = parseResponse(okHttpClient.newCall(createRechargeRequest("source",
                            source.id!!
                        )).execute())

                        val result = Klaxon()
                            .parse<OmiseRechargeQrResp>(parsedResponse)

                        if (result?.data != null) {
                            val qrImage = result.data.qr_image
                            println("url: $qrImage")
                            showQRImageActivity(qrImage)

                        }
                    }

                    // Credit Card
                } else if (it.data?.hasExtra(OmiseActivity.EXTRA_TOKEN) == true) {
                    val token = it.data?.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT, Token::class.java)
                    println("token: " + token?.toString())

                    if (token?.id != null) {
                        val okHttpClient = OkHttpClient()
                        val parsedResponse = parseResponse(okHttpClient.newCall(createRechargeRequest("card",
                            token.id!!
                        )).execute())

                        val result = Klaxon()
                            .parse<OmiseRechargeCardResp>(parsedResponse)

                        if (result?.data != null) {
                            val authorizedUrl = result.data.authorize_uri
                            println("url: $authorizedUrl")
                            showAuthorizingPaymentForm(authorizedUrl)
                        }
                    }
                }

                return@registerForActivityResult
            }
        }



    fun onCCPaymentClick(view: View?) {
//        makeRequest("CC")
        // your code to perform when the user clicks on the button
//        Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()

        showPaymentCreatorActivity()
    }

    fun onQRPaymentClick(view: View?) {
//        makeRequest("QR")
    }
}