package ufg.go.br.recrutame

import android.os.Bundle

class SplaceScreenActivity : SplashPermissionsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splace_screen)
    }

    override fun getNextActivityClass(): Class<*> {
        return TabActivity::class.java
    }
}
