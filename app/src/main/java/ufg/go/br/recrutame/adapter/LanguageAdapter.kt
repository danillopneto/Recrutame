package ufg.go.br.recrutame.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserLanguageInfo
import android.widget.ListView

class LanguageAdapter(private var languages: List<UserLanguageInfo>, private var activity: Activity) : BaseAdapter() {

    override fun getCount(): Int {
        return languages.size
    }

    override fun getItem(position: Int): Any? {
        return languages[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = activity.layoutInflater
                .inflate(R.layout.list_language_item, parent, false)
        val language = languages[position]
        val languageTxt = view.findViewById<TextView>(R.id.mLanguageTxt)
        languageTxt.text = language.language

        val levelTxt = view.findViewById<TextView>(R.id.mLanguageLevelTxt)
        levelTxt.text = activity.getString(language.level!!.idString)

        val editLanguageBtn = view.findViewById<ImageButton>(R.id.mEditLanguagueBtn)
        editLanguageBtn.setOnClickListener { v ->
            (parent as ListView).performItemClick(v, position, 0)
        }

        return view
    }
}