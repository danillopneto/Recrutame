package ufg.go.br.recrutame.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ufg.go.br.recrutame.R

class ItemProfileAdapter(atividades: MutableList<String>) : RecyclerView.Adapter<SkillHolder>() {

    private val mAtividades: MutableList<String> = atividades

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): SkillHolder {
        return SkillHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_atividades , parent , false))
    }


    override fun onBindViewHolder(holder: SkillHolder , position: Int) {
        holder.addItem.text = mAtividades[position]
        holder.delete.setOnClickListener { view ->
            removeItem(position)
        }
    }

        override fun getItemCount(): Int {
            return mAtividades.size
        }

        fun getItens(): MutableList<String> {
            return mAtividades
        }

        fun updateList(atividade: String) {
            insertItem(atividade)
            notifyDataSetChanged()
        }

        private fun insertItem(atividade: String) {
            mAtividades.add(0 , atividade)
            notifyItemInserted(itemCount)
            notifyDataSetChanged()
        }

        private fun removeItem(position: Int) {
            mAtividades.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position , mAtividades.count())
            notifyDataSetChanged()
        }



}