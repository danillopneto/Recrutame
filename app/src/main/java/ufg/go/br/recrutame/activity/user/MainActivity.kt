package ufg.go.br.recrutame.activity.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.TabActivity
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
            R.id.mLoginButton ->  handleLogin(mEmailTxt.text.toString(), mPasswordTxt.text.toString(), false,false)
            R.id.mRegisterButton -> handleRegister()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, TabActivity:: class.java))
        }
    }

    private fun handleForgotPassword() {
        startActivity(Intent(this, ForgotPasswordActivity:: class.java))
    }

    private fun handleLinkedInLogin() {
        startActivity(Intent(this, LILoginActivity :: class.java))
    }

    private fun handleRegister() {
        hideKeyboard()
        startActivity(Intent(this, RegisterActivity:: class.java))
    }

    private fun inicializeControls() {
        mActionButton = findViewById(R.id.mLoginButton)
        forgotPasswordBtn.setOnClickListener(this)
        mLoginButton.setOnClickListener(this)
        linkedinLoginBtn.setOnClickListener(this)
        mRegisterButton.setOnClickListener(this)
    }

}
