package ufg.go.br.recrutame.api.activities

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

    private fun initializeControls() {
        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBarLI)
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

                    val client = ServiceGenerator(LI_BASE_URL).createService(LIService::class.java)
                    val call = client.getNewAccessToken(access_token)

                    progressBar.visibility = View.VISIBLE
                    call.enqueue(object : Callback<LIAccessToken> {
                        override fun onResponse(call: Call<LIAccessToken>, response: Response<LIAccessToken>) {
                            progressBar.visibility = View.INVISIBLE
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                val token = response.body()
                                fillLinkedinInfo(token?.accessToken.toString())
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
                }  else if (url.contains("?error")) {
                    Toast.makeText(applicationContext, getString(R.string.linkedin_login_failed), Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.INVISIBLE
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

            progressBar.visibility = View.VISIBLE
            call.enqueue(object : Callback<LIProfileInfo> {
                override fun onResponse(call: Call<LIProfileInfo>, response: Response<LIProfileInfo>) {
                    progressBar.visibility = View.INVISIBLE
                    val statusCode = response.code()
                    if (statusCode == 200) {
                        liProfileInfo = response.body()!!
                        setNewUserData(liProfileInfo.getFullName(), liProfileInfo.pictureUrl!!)
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
        progressBar.visibility = View.VISIBLE
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (task.result.providers!!.size > 0) {
                    handleLogin(email, LINKEDIN_PASSWORD, false, true)
                } else {
                    mAuth.createUserWithEmailAndPassword(email, LINKEDIN_PASSWORD).addOnCompleteListener(this) { task ->
                        progressBar.visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                            mAuth.currentUser?.updateProfile(profileUpdates.build())
                            handleLogin(email, LINKEDIN_PASSWORD, true, true)
                        } else {
                            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}
