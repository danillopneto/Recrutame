package ufg.go.br.recrutame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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
            R.id.forgotPasswordBtn -> handleForgotPassword()
            R.id.linkedinLoginBtn -> handleLinkedInLogin()
            R.id.loginBtn -> handleLogin(emailTxt.text.toString(), passwordTxt.text.toString())
            R.id.registerBtn -> handleRegister()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, TabActivity :: class.java))
        } else {
            val user = preferences.getUserEmail()
            if (!user.isEmpty()) {
                handleLogin(user, LINKEDIN_PASSWORD)
            }
        }
    }

    override fun getProgressBar(): ProgressBar {
        return progressBar
    }

    private fun handleForgotPassword() {
        startActivity(Intent(this, ForgotPasswordActivity :: class.java))
    }

    private fun handleLinkedInLogin() {
        startActivity(Intent(this, LILoginActivity :: class.java))
    }

    private fun handleRegister() {
        hideKeyboard()
        startActivity(Intent(this, RegisterActivity :: class.java))
    }

    private fun inicializeControls() {
        forgotPasswordBtn.setOnClickListener(this)
        loginBtn.setOnClickListener(this)
        linkedinLoginBtn.setOnClickListener(this)
        registerBtn.setOnClickListener(this)
    }

}
