package ufg.go.br.recrutame.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserLanguageInfo
import android.view.LayoutInflater

class LanguageAdapter(
                      private var languages: List<UserLanguageInfo>,
                      private var onClickListener: LanguageAdapterListener,
                      private var activity: Activity)
        : RecyclerView.Adapter<LanguageAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var languageDescription: TextView = view.findViewById(R.id.mLanguageTxt)
        var level: TextView = view.findViewById(R.id.mLanguageLevelTxt)
        var editButton: ImageButton = view.findViewById(R.id.mEditLanguagueBtn)

        init {
            editButton.setOnClickListener { v -> onClickListener.iconImageViewOnClick(v, adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_language_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val language = languages[position]
        holder.languageDescription.text = language.language
        holder.level.text = activity.getString(language.level!!.idString)
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    interface LanguageAdapterListener {
        fun iconImageViewOnClick(v: View, position: Int)
    }
}