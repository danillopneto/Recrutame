package ufg.go.br.recrutame.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import net.orange_box.storebox.StoreBox
import ufg.go.br.recrutame.model.MyPreferences

abstract class BaseActivity : AppCompatActivity() {
    fun getMyPreferences(): MyPreferences {
        return StoreBox.create(this, MyPreferences::class.java)
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}