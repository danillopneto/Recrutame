package ufg.go.br.recrutame.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ufg.go.br.recrutame.model.MatchItemList
import android.widget.TextView
import ufg.go.br.recrutame.R


class MatchAdapter(var matches:List<MatchItemList>) : RecyclerView.Adapter<MatchAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val companyName = view.findViewById(R.id.mCompanyNameTxt) as TextView
        val lastMessage = view.findViewById(R.id.mMessageText) as TextView

        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchAdapter.MyViewHolder {
        val layout = R.layout.fragment_chat_item

        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MatchAdapter.MyViewHolder, position: Int){
        val match = matches[position]
        holder.companyName.text = match.companyName
        holder.lastMessage.text = match.message
    }

    override fun getItemCount(): Int {
        return matches.size
    }
}