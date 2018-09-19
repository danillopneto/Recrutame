package ufg.go.br.recrutame.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ufg.go.br.recrutame.model.MatchItemList
import android.widget.TextView
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.util.ImageUtils
import ufg.go.br.recrutame.activity.MessageListActivity


class MatchAdapter(var matches:List<MatchItemList>, var userId: String) : RecyclerView.Adapter<MatchAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val companyName = view.findViewById(R.id.mCompanyNameTxt) as TextView
        val lastMessage = view.findViewById(R.id.mMessageText) as TextView
        var companyImg = view.findViewById(R.id.chat_companyImg) as ImageView

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
        ImageUtils.displayRoundImageFromUrl(match?.companyImg!!, holder.companyImg)

        holder.itemView.setOnClickListener(View.OnClickListener {
            var intent = Intent(it.context, MessageListActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("jobId", match.jobId)
            it.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return matches.size
    }
}