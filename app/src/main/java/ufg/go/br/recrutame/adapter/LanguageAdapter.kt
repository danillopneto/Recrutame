package ufg.go.br.recrutame.adapter

import android.app.Activity
import android.view.View
import ufg.go.br.recrutame.model.UserLanguageInfo

class LanguageAdapter(
                      private var languages: List<UserLanguageInfo>,
                      private var activity: Activity,
                      editMode: Boolean,
                      onClickListener: ProfileInfoAdapter.ProfileInfoAdapterListener?)
    : ProfileInfoAdapter<UserLanguageInfo>(languages, editMode, onClickListener) {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val language = languages[position]
        holder.title.text = language.language
        holder.subtitle.text = activity.getString(language.level!!.idString)
        holder.period.visibility = View.GONE
    }
}