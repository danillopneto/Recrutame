package ufg.go.br.recrutame

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import ufg.go.br.recrutame.api.activities.LILoginActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginbutton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.loginbutton) {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val prefs = application.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val loggedIn = prefs != null
                && prefs.contains(OAUTH_ACCESSTOKEN)
                && prefs.getString(OAUTH_ACCESSTOKEN, "") != ""

        val intent = Intent(this, if (loggedIn) TabActivity::class.java else LILoginActivity::class.java )
        startActivity(intent)
    }
}
