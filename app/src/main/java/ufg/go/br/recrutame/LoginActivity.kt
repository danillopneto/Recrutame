package ufg.go.br.recrutame

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufg.go.br.recrutame.api.model.AccessToken
import ufg.go.br.recrutame.api.service.LIService
import ufg.go.br.recrutame.api.service.ServiceGenerator

class LoginActivity : Activity() {

    private val url = "$AUTHORIZATION_URL?response_type=code&client_id=$API_KEY&redirect_uri=$REDIRECT_URI&scope=r_basicprofile"

    private lateinit var web_view: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeWebView()
    }

    private fun initializeWebView() {
        web_view = findViewById(R.id.web_view)
        web_view.loadUrl(url)
        web_view.webViewClient = object : WebViewClient() {
            internal var authComplete = false

            lateinit var access_token: String

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val uri = Uri.parse(url)

                if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
                    web_view.visibility = View.GONE
                    // use the parameter your API exposes for the code (mostly it's "code")
                    val code = uri.getQueryParameter("code")
                    if (code != null) {
                        // get access token
                        // we'll do that in a minute
                    } else if (uri.getQueryParameter("error") != null) {
                        // show an error message here
                    }
                }

                if (url.contains("$RESPONSE_TYPE_VALUE=") && !authComplete) {
                    // get the whole token after the '=' sign
                    access_token = url.substring(url.lastIndexOf("=") + 1)
                    Log.i("", "CODE : $access_token")
                    authComplete = true

                    val prefs = application.getSharedPreferences(
                            BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

                    val client = ServiceGenerator().createService(LIService::class.java)
                    val call = client.getNewAccessToken(access_token)

                    call.enqueue(object : Callback<AccessToken> {
                        override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                val token = response.body()
                                prefs.edit().putBoolean("oauth.loggedin", true).apply()
                                prefs.edit().putString("oauth.accesstoken", token!!.accessToken).apply()
                                prefs.edit().putString("oauth.refreshtoken", token.refreshToken).apply()

                                // TODO Show the user they are logged in
                                Toast.makeText(applicationContext, "Logged in", Toast.LENGTH_SHORT).show()
                            } else {
                                // TODO Handle errors on a failed response
                                Toast.makeText(applicationContext, "Failed in", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                            // TODO Handle failure
                            Toast.makeText(applicationContext, "Failed in", Toast.LENGTH_SHORT).show()
                        }
                    })
                    finish()
                } else if (url.contains("?error")) {
                    Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
