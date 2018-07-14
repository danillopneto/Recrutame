package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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

    fun handleLogin(email: String, password: String) {
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    this.finishAffinity()
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

    fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }
}
