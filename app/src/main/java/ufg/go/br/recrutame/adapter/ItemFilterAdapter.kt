package ufg.go.br.recrutame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import ufg.go.br.recrutame.R


class ItemFilterAdapter(filters: MutableList<String>) : RecyclerView.Adapter<LineHolder>() {

    private val mFilters: MutableList<String> = filters

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineHolder {
        return LineHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_filter, parent, false))
    }

    override fun onBindViewHolder(holder: LineHolder, position: Int) {
        holder.filterItem.text = mFilters[position]
        holder.deleteButton.setOnClickListener { view -> removeItem(position) }
    }

    override fun getItemCount(): Int {
        return mFilters.size
    }

    fun getItens(): MutableList<String> {
        return mFilters
    }

    fun updateList(filter: String) {
        insertItem(filter)
        notifyDataSetChanged()
    }

    private fun insertItem(filter: String) {
        mFilters.add(0, filter)
        notifyItemInserted(itemCount)
        notifyDataSetChanged()
    }

    private fun removeItem(position: Int) {
        mFilters.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mFilters.count())
        notifyDataSetChanged()
    }
}