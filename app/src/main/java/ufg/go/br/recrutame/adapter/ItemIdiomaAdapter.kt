package ufg.go.br.recrutame.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ufg.go.br.recrutame.R

class ItemIdiomaAdapter(idiomas: MutableList<String>) : RecyclerView.Adapter<IdiomHolder>() {

    private val mIdiomas: MutableList<String> = idiomas

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): IdiomHolder {
        return IdiomHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_idioma , parent , false))
    }


    override fun onBindViewHolder(holder: IdiomHolder , position: Int) {
        holder.addItem.text = mIdiomas[position]
        holder.delete.setOnClickListener { view ->
            removeItem(position)
        }
    }

        override fun getItemCount(): Int {
            return mIdiomas.size
        }

        fun getItens(): MutableList<String> {
            return mIdiomas
        }

        fun updateList(idioma: String) {
            insertItem(idioma)
            notifyDataSetChanged()
        }

        private fun insertItem(idioma: String) {
            mIdiomas.add(0 , idioma)
            notifyItemInserted(itemCount)
            notifyDataSetChanged()
        }

        private fun removeItem(position: Int) {
            mIdiomas.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position , mIdiomas.count())
            notifyDataSetChanged()
        }



}