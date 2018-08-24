package ufg.go.br.recrutame.activity.profile

import android.content.Intent
import android.os.Bundle
import ufg.go.br.recrutame.R
import android.view.MenuItem

class EditLanguagesInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mLanguageInfoLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_languages_info)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish(); return true; }
            R.id.mMenuAddProfile -> saveInfo()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun saveInfo() {
        val i = Intent(this, AddEditLanguageInfoActivity:: class.java)
        i.putExtra("userId", userId)
        i.putExtra("title", getString(R.string.add_language))
        startActivity(i)
    }

    override fun getActionMenu(): Int {
        return R.menu.menu_action_edit_add
    }

    override fun getActionBarTitle(): String {
        return getString(R.string.languages)
    }
}
