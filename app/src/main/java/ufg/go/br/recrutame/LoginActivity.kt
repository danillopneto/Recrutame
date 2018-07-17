package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_main.*
import ufg.go.br.recrutame.api.model.LIProfileInfo

abstract class LoginActivity : AppCompatActivity() {
    lateinit var liProfileInfo: LIProfileInfo
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, TabActivity :: class.java))
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }

    fun handleLogin(email: String, password: String) {
        hideKeyboard()
        if (!email.isEmpty() && !password.isEmpty()) {
            getProgressBar().visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                getProgressBar().visibility = View.INVISIBLE
                if (!task.isSuccessful) {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseNetworkException) {
                        Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                        Log.e(TAG, e.message)
                    }
                } else {
                    this.finishAffinity()
                    getSharedPreferences().edit().putString(USER_EMAIL, email).apply()
                    startActivity(Intent(this, TabActivity :: class.java))
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    abstract fun getProgressBar(): ProgressBar
}
