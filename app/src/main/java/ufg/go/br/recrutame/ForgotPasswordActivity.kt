package ufg.go.br.recrutame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import android.text.TextUtils
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        mAuth = FirebaseAuth.getInstance()
        resetPasswordBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.resetPasswordBtn -> handleResetPassword()
        }
    }

    private fun handleResetPassword() {
        val email = emailTxt.text.toString()
        if (!validateForm(email)) {
            return
        }

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
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
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, getString(R.string.type_email_address), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
