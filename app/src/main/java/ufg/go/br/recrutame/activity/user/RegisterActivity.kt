package ufg.go.br.recrutame.activity.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.util.Utils

class RegisterActivity : LoginActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        inicializeApis()
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mRegisterButton -> handleRegister()
        }
    }

    private fun handleRegister() {
        hideKeyboard()
        if (validateForm()) {
            registerUser(mEmailTxt.text.toString(), mPasswordTxt.text.toString())
        }
    }

    private fun inicializeControls() {
        mActionButton = findViewById(R.id.mRegisterButton)
        mRegisterButton.setOnClickListener(this)
    }

    private fun registerUser(email: String, password: String) {
        mActionButton?.startAnimation()
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                mActionButton?.revertAnimation()
                try {
                    throw task.exception!!}
                catch (e: FirebaseNetworkException) {
                    Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthWeakPasswordException) {
                    Toast.makeText(this, getString(R.string.error_weak_password), Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show()
                } catch (e: FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, getString(R.string.error_user_exists), Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_LONG).show()
                    Log.e(ufg.go.br.recrutame.util.TAG, e.message)
                }
            } else {
                Toast.makeText(this, getString(R.string.user_registered), Toast.LENGTH_LONG).show()
                insertUserData(task.result.user.uid, email) {
                    handleLogin(email, password, true, false)
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        return if (!Utils.isNullOrWhiteSpace(mEmailTxt.text.toString())
                && !Utils.isNullOrWhiteSpace(mPasswordTxt.text.toString())
                && !Utils.isNullOrWhiteSpace(mConfirmPasswordTxt.text.toString())) {
            if (mPasswordTxt.text.toString() == mConfirmPasswordTxt.text.toString()) {
                true
            } else {
                Toast.makeText(this, getString(R.string.password_doesnt_match), Toast.LENGTH_LONG).show()
                false
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
            false
        }
    }
}
