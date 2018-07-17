package ufg.go.br.recrutame

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterActivity : LoginActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.registerBtn -> handleRegister()
        }
    }

    override fun getProgressBar(): ProgressBar {
        return progressBarRegister
    }

    private fun handleRegister() {
        hideKeyboard()
        if (!emailTxt.text.isEmpty()
                && !passwordTxt.text.isEmpty()
                && !confirmPasswordTxt.text.isEmpty()) {
            if (passwordTxt.text.toString() == confirmPasswordTxt.text.toString()) {
                registerUser(emailTxt.text.toString(), passwordTxt.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.password_doesnt_match), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
        }
    }

    private fun inicializeControls() {
        mAuth = FirebaseAuth.getInstance()
        registerBtn.setOnClickListener(this)
    }

    private fun registerUser(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    Toast.makeText(this, getString(R.string.error_weak_password), Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, getString(R.string.error_user_exists), Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_LONG).show()
                    Log.e(TAG, e.message)
                }
            } else {
                Toast.makeText(this, getString(R.string.user_registered), Toast.LENGTH_LONG).show()
                handleLogin(email, password)
            }
        }
    }
}
