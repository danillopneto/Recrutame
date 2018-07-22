package ufg.go.br.recrutame.fragment

import android.widget.ImageButton
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import ufg.go.br.recrutame.R

class LineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var filterItem: TextView = itemView.findViewById(R.id.filterItemTxt)
    var deleteButton: ImageButton = itemView.findViewById(R.id.deleteFilterItem)
}