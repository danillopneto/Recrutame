package ufg.go.br.recrutame.activity

import android.support.v7.app.AppCompatActivity
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.model.MyPreferences

abstract class BaseActivity : AppCompatActivity() {
    fun getMyPreferences(): MyPreferences {
        return StoreBox.create(this, MyPreferences::class.java)
    }
}