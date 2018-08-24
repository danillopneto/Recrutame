package ufg.go.br.recrutame.activity.user

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import android.util.Log
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import ufg.go.br.recrutame.util.LINKEDIN_PASSWORD
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.util.Utils

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
        mAuth.signInWithEmailAndPassword(email, LINKEDIN_PASSWORD).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
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
                                    Log.e(ufg.go.br.recrutame.util.TAG, e.message)
                                }
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.email_reset_password), Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
            } else {
                Toast.makeText(this, getString(R.string.user_created_with_linkedin), Toast.LENGTH_LONG).show()
                mAuth.signOut()
                mResetPasswordButton.revertAnimation()
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
