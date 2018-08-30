package ufg.go.br.recrutame.activity

import android.os.Bundle
import ufg.go.br.recrutame.R

class SplashScreenActivity : SplashPermissionsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun getNextActivityClass(): Class<*> {
        return TabActivity::class.java
    }
}
