package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
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

        mAuth = FirebaseAuth.getInstance()
        loginBtn.setOnClickListener(this)
        linkedinLoginBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginBtn -> handleLogin()
            R.id.linkedinLoginBtn -> handleLinkedInLogin()
        }
    }

    private fun handleLogin() {
        val emailTxt = findViewById<EditText>(R.id.emailTxt)
        val passwordTxt = findViewById<EditText>(R.id.passwordTxt)

        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, TabActivity :: class.java))
                } else {
                    if (task.exception != null) {
                        Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                    }
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
        }
    }

    private fun handleLinkedInLogin() {
        val prefs = application.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val loggedIn = prefs != null
                && prefs.contains(OAUTH_ACCESSTOKEN)
                && prefs.getString(OAUTH_ACCESSTOKEN, "") != ""

        val activity = if (loggedIn) TabActivity :: class.java else LILoginActivity :: class.java
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}
