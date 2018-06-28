package ufg.go.br.recrutame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.APIHelper
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var imgLogin: ImageView
    lateinit var imgProfile : ImageView
    lateinit var txtDetails: TextView
    lateinit var btnLogout : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializeControls()
    }

    private fun inicializeControls() {
        imgLogin = findViewById(R.id.imgLogin)
        imgLogin.setOnClickListener(this)
        imgProfile = findViewById(R.id.imgProfile)
        txtDetails = findViewById(R.id.txtDetails)
        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener(this)

        setStartLayout()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgLogin -> handleLogin()
            R.id.btnLogout -> handleLogout()
        }
    }

    private fun handleLogin() {
        LISessionManager.getInstance(applicationContext).init(this, buildScope(), object : AuthListener {
            override fun onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                fetchPersonalInfo()
                imgLogin.visibility = View.GONE
                imgProfile.visibility = View.VISIBLE
                txtDetails.visibility = View.VISIBLE
                btnLogout.visibility = View.VISIBLE
            }

            override fun onAuthError(error: LIAuthError) {
                // Handle authentication errors
                Log.e("CDV", error.toString())
            }
        }, true)
    }

    private fun setStartLayout() {
        imgLogin.visibility = View.VISIBLE
        imgProfile.visibility = View.GONE
        txtDetails.visibility = View.GONE
        btnLogout.visibility = View.GONE
    }

    private fun handleLogout() {
        LISessionManager.getInstance(applicationContext).clearSession()
        setStartLayout()
    }

    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(applicationContext).onActivityResult(this, requestCode, resultCode, data)
    }

    private fun fetchPersonalInfo() {
        val url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))"

        val apiHelper = APIHelper.getInstance(applicationContext)
        apiHelper.getRequest(this, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse) {
                // Success!
                val jsonObject = apiResponse.responseDataAsJson
                val firstName = jsonObject.getString("firstName")
                val lastName = jsonObject.getString("lastName")
                val pictureUrl = jsonObject.getString("pictureUrl")
                val emailAddress = jsonObject.getString("emailAddress")

                Picasso.get().load(pictureUrl).into(imgProfile)
                val sb = StringBuilder()
                sb.append("Nome: $firstName $lastName")
                sb.append("\n\n")
                sb.append("E-mail: $emailAddress")
                txtDetails.text = sb.toString()
            }

            override fun onApiError(liApiError: LIApiError) {
                // Error making GET request!
                Log.e("CDV", liApiError.message)
            }
        })
    }
}
