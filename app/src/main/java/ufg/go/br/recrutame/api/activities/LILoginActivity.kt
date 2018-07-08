package ufg.go.br.recrutame.api.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufg.go.br.recrutame.*
import ufg.go.br.recrutame.api.model.LIAccessToken
import ufg.go.br.recrutame.api.service.LIService
import ufg.go.br.recrutame.api.service.ServiceGenerator

class LILoginActivity : Activity() {

    private val url = "$AUTHORIZATION_URL?$RESPONSE_TYPE_PARAM=$RESPONSE_TYPE_VALUE&$CLIENT_ID_PARAM=$CLIENT_ID&$REDIRECT_URI_PARAM=$REDIRECT_URI&$SCOPE_PARAM=$SCOPES"

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeWebView()
    }

    private fun initializeWebView() {
        webView = findViewById(R.id.web_view)
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            var authComplete = false

            lateinit var access_token: String

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (url.contains("$RESPONSE_TYPE_VALUE=") && !authComplete) {
                    webView.visibility = View.GONE
                    // get the whole token after the '=' sign
                    access_token = url.substring(url.lastIndexOf("=") + 1)
                    Log.i("", "CODE : $access_token")
                    authComplete = true

                    val prefs = application.getSharedPreferences(
                            BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

                    val client = ServiceGenerator(LI_BASE_URL).createService(LIService::class.java)
                    val call = client.getNewAccessToken(access_token)

                    call.enqueue(object : Callback<LIAccessToken> {
                        override fun onResponse(call: Call<LIAccessToken>, response: Response<LIAccessToken>) {
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                val token = response.body()
                                prefs.edit().putBoolean(OAUTH_LOGGEDIN, true).apply()
                                prefs.edit().putString(OAUTH_ACCESSTOKEN, token!!.accessToken).apply()

                                Toast.makeText(applicationContext, "Logged in", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, TabActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Failed in", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LIAccessToken>, t: Throwable) {
                            Toast.makeText(applicationContext, "Failed in", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else if (url.contains("?error")) {
                    Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
