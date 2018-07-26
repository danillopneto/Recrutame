package ufg.go.br.recrutame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import android.text.TextUtils
import android.util.Log
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import ufg.go.br.recrutame.Util.Utils

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mResetPasswordButton: CircularProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        mAuth = FirebaseAuth.getInstance()
        mResetPasswordButton = findViewById(R.id.mResetPasswordButton)
        mResetPasswordButton.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mResetPasswordButton.dispose()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mResetPasswordButton -> handleResetPassword()
        }
    }

    private fun handleResetPassword() {
        val email = mEmailTxt.text.toString()
        if (!validateForm(email)) {
            return
        }

        mResetPasswordButton.startAnimation()
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                mResetPasswordButton.revertAnimation()
                if (!task.isSuccessful) {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseNetworkException) {
                        Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, getString(R.string.no_user_found), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.reset_password_failed), Toast.LENGTH_LONG).show()
                        Log.e(TAG, e.message)
                    }
                } else {
                    Toast.makeText(applicationContext, getString(R.string.email_reset_password), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
    }

    private fun validateForm(email: String): Boolean {
        if (Utils.isNullOrWhiteSpace(email)) {
            Toast.makeText(applicationContext, getString(R.string.type_email_address), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
