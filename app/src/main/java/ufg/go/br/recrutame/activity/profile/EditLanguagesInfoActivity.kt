package ufg.go.br.recrutame.activity.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import ufg.go.br.recrutame.R
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ufg.go.br.recrutame.adapter.LanguageAdapter
import ufg.go.br.recrutame.model.UserLanguageInfo
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View

class EditLanguagesInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mLanguageInfoLayout
    private var languages = mutableListOf<UserLanguageInfo>()
    private lateinit var mLanguagesRv: RecyclerView
    private lateinit var mLanguageAdapter: LanguageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_languages_info)
        inicializeControls()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish(); return true; }
            R.id.mMenuAddItem -> saveInfo()
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
        mLanguagesRv = findViewById(R.id.mLanguagesRv)
        mLanguageAdapter = LanguageAdapter(languages, object : LanguageAdapter.LanguageAdapterListener {
            override fun iconImageViewOnClick(v: View, position: Int) {
                val i = Intent(baseContext,  AddEditLanguageInfoActivity:: class.java)
                i.putExtra("userId", userId)
                i.putExtra("languageKey", languages[position].key)
                i.putExtra("languageDescription", languages[position].language)
                i.putExtra("languageLevel", languages[position].level.toString())
                startActivity(i)
            }
        }, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLanguagesRv.layoutManager = mLayoutManager
        mLanguagesRv.itemAnimator = DefaultItemAnimator()
        mLanguagesRv.adapter = mLanguageAdapter

        mLanguageAdapter.notifyDataSetChanged()
    }
}
