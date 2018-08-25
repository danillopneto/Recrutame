package ufg.go.br.recrutame.activity.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import ufg.go.br.recrutame.R
import android.view.MenuItem
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ufg.go.br.recrutame.adapter.LanguageAdapter
import ufg.go.br.recrutame.model.UserLanguageInfo
import android.widget.AdapterView

class EditLanguagesInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mLanguageInfoLayout
    private lateinit var mLanguagesLv: ListView
    private var languages = mutableListOf<UserLanguageInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_languages_info)
        inicializeControls()
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
        i.putExtra("languageKey", "")
        startActivity(i)
    }

    override fun getActionMenu(): Int {
        return R.menu.menu_action_edit_add
    }

    override fun getActionBarTitle(): String {
        return getString(R.string.languages)
    }

    private fun inicializeControls() {
        mLanguagesLv = findViewById(R.id.mLanguagesLv)

        mDatabase.child("users/$userId/languages")
                .orderByChild("language")
                .addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        languages.clear()
                        for (child in dataSnapshot!!.children) {
                            val language = child.getValue(UserLanguageInfo::class.java)
                            if (language != null) {
                                languages .add(language)
                            }
                        }

                        inflateLanguageAdapter(languages)
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        Log.d(ufg.go.br.recrutame.util.TAG, "" + databaseError)
                    }
                })
    }

    private fun inflateLanguageAdapter(languages: List<UserLanguageInfo>) {
        val adapter = LanguageAdapter(languages, this)
        mLanguagesLv.adapter = adapter
        mLanguagesLv.onItemClickListener = AdapterView.OnItemClickListener { adapter, view, position, arg ->
            if (view.id == R.id.mEditLanguagueBtn) {
                val i = Intent(baseContext,  AddEditLanguageInfoActivity:: class.java)
                i.putExtra("userId", userId)
                i.putExtra("languageKey", languages[position].key)
                i.putExtra("languageDescription", languages[position].language)
                i.putExtra("languageLevel", languages[position].level.toString())
                startActivity(i)
            }
        }
    }
}
