package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import ufg.go.br.recrutame.api.activities.LILoginActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginBtn -> handleLogin(emailTxt.text.toString(), passwordTxt.text.toString())
            R.id.linkedinLoginBtn -> handleLinkedInLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        setLastUser()
    }

    private fun setLastUser() {
        val prefs = application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val user = prefs.getString(USER_EMAIL, "")
        if (user != null) {
            emailTxt.setText(user)
            passwordTxt.isFocusable = true
        }
    }

    private fun inicializeControls() {
        mAuth = FirebaseAuth.getInstance()
        loginBtn.setOnClickListener(this)
        linkedinLoginBtn.setOnClickListener(this)
        setLastUser()
    }

    private fun handleLogin(email: String, password: String) {
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    getSharedPreferences().edit().putString(USER_EMAIL, email).apply()
                    startActivity(Intent(this, TabActivity :: class.java))
                } else {
                    if (task.exception != null) {
                        Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
        }
    }

    private fun handleLinkedInLogin() {
        val prefs = getSharedPreferences()
        val loggedIn = prefs != null
                && prefs.contains(LINKEDIN_USER)
                && prefs.getString(LINKEDIN_USER, "") != ""
        if (loggedIn) {
            val email = prefs.getString(LINKEDIN_USER, "")
            mAuth.fetchProvidersForEmail(email).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (task.result.providers!!.size > 0) {
                        handleLogin(email, LINKEDIN_PASSWORD)
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, LINKEDIN_PASSWORD).addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                handleLogin(email, LINKEDIN_PASSWORD)
                            } else {
                                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                } else {
                    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            startActivity(Intent(this, LILoginActivity :: class.java))
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }
}
