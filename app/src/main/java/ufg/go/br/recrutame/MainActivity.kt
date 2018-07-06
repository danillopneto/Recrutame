package ufg.go.br.recrutame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginButton : Button = findViewById(R.id.loginbutton)
        loginButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.loginbutton) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
