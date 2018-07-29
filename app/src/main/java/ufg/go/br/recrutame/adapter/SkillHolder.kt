package ufg.go.br.recrutame.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import ufg.go.br.recrutame.R

class SkillHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var addItem: TextView = itemView.findViewById(R.id.atividadesItemTxt)
    var delete: ImageButton = itemView.findViewById(R.id.deleteAtividadeItem)
}