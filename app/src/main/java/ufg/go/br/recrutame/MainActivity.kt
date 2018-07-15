package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ufg.go.br.recrutame.api.activities.LILoginActivity

class MainActivity : LoginActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginBtn -> handleLogin(emailTxt.text.toString(), passwordTxt.text.toString())
            R.id.linkedinLoginBtn -> handleLinkedInLogin()
            R.id.registerBtn -> handleRegister()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, TabActivity :: class.java))
        } else {
            val prefs = application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            val user = prefs.getString(LINKEDIN_USER, "")
            if (!user.isEmpty()) {
                handleLogin(user, LINKEDIN_PASSWORD)
            }
        }
    }

    private fun inicializeControls() {
        loginBtn.setOnClickListener(this)
        linkedinLoginBtn.setOnClickListener(this)
        registerBtn.setOnClickListener(this)
    }

    private fun handleLinkedInLogin() {
        startActivity(Intent(this, LILoginActivity :: class.java))
    }

    private fun handleRegister() {
        startActivity(Intent(this, RegisterActivity :: class.java))
    }
}
