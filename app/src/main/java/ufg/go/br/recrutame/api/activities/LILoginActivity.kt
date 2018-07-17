package ufg.go.br.recrutame.api.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufg.go.br.recrutame.*
import ufg.go.br.recrutame.api.model.LIAccessToken
import ufg.go.br.recrutame.api.model.LIProfileInfo
import ufg.go.br.recrutame.api.service.LIService
import ufg.go.br.recrutame.api.service.ServiceGenerator

class LILoginActivity : LoginActivity() {
    private val url = "$AUTHORIZATION_URL?$RESPONSE_TYPE_PARAM=$RESPONSE_TYPE_VALUE&$CLIENT_ID_PARAM=$CLIENT_ID&$REDIRECT_URI_PARAM=$REDIRECT_URI&$SCOPE_PARAM=$SCOPES"

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeControls()
    }

    override fun getProgressBar(): ProgressBar {
        return progressBarLI
    }

    private fun initializeControls() {
        mAuth = FirebaseAuth.getInstance()
        webView = findViewById(R.id.web_view)
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            var authComplete = false

            lateinit var access_token: String

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (url.contains("$RESPONSE_TYPE_VALUE=") && !authComplete) {
                    webView.visibility = View.GONE

                    access_token = url.substring(url.lastIndexOf("=") + 1)
                    Log.i("", "CODE : $access_token")
                    authComplete = true

                    val prefs = application.getSharedPreferences(
                            BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

                    val client = ServiceGenerator(LI_BASE_URL).createService(LIService::class.java)
                    val call = client.getNewAccessToken(access_token)

                    getProgressBar().visibility = View.VISIBLE
                    call.enqueue(object : Callback<LIAccessToken> {
                        override fun onResponse(call: Call<LIAccessToken>, response: Response<LIAccessToken>) {
                            getProgressBar().visibility = View.INVISIBLE
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                val token = response.body()
                                prefs.edit().putBoolean(OAUTH_LOGGEDIN, true).apply()
                                prefs.edit().putString(OAUTH_ACCESSTOKEN, token!!.accessToken).apply()
                                fillLinkedinInfo(token.accessToken.toString())
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.linkedin_login_failed), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LIAccessToken>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.linkedin_login_failed), Toast.LENGTH_SHORT).show()
                        }
                    })
                } else if (url.contains("user_cancelled")) {
                    finish()
                }
                else if (url.contains("?error")) {
                    Toast.makeText(applicationContext, getString(R.string.linkedin_login_failed), Toast.LENGTH_SHORT).show()
                    getProgressBar().visibility = View.INVISIBLE
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                webView.loadUrl("file:///android_asset/error.html")
            }
        }
    }

    private fun fillLinkedinInfo(token: String) {
        if (token != "") {
            val client = ServiceGenerator(API_BASE_URL).createService(LIService::class.java)
            val map = HashMap<String, String>()
            map["Authorization"] = "Bearer $token"
            val call = client.getUserInfo(map)

            getProgressBar().visibility = View.VISIBLE
            call.enqueue(object : Callback<LIProfileInfo> {
                override fun onResponse(call: Call<LIProfileInfo>, response: Response<LIProfileInfo>) {
                    getProgressBar().visibility = View.INVISIBLE
                    val statusCode = response.code()
                    if (statusCode == 200) {
                        liProfileInfo = response.body()!!
                        application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                                .edit().putString(LINKEDIN_USER, liProfileInfo.emailAddress).apply()
                        handleFirebaseLogin(liProfileInfo.emailAddress!!)
                    } else {
                        Toast.makeText(applicationContext, "Failed in", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LIProfileInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, "Failed in", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        }
    }

    private fun handleFirebaseLogin(email: String) {
        getProgressBar().visibility = View.VISIBLE
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (task.result.providers!!.size > 0) {
                    handleLogin(email, LINKEDIN_PASSWORD)
                } else {
                    mAuth.createUserWithEmailAndPassword(email, LINKEDIN_PASSWORD).addOnCompleteListener(this) { task ->
                        getProgressBar().visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                            handleLogin(email, LINKEDIN_PASSWORD)
                        } else {
                            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                getProgressBar().visibility = View.INVISIBLE
            }
        }
    }
}
