package ufg.go.br.recrutame.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import ufg.go.br.recrutame.R

abstract class ProfileInfoAdapter<T>(
                                     private var items: List<T>,
                                     private var editMode: Boolean,
                                     private var onClickListener: ProfileInfoAdapterListener?)  : RecyclerView.Adapter<ProfileInfoAdapter<T>.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.mProfileInfoTitleTxt)
        var subtitle: TextView = view.findViewById(R.id.mProfileInfoSubtitleTxt)
        var period: TextView = view.findViewById(R.id.mProfileInfoPeriodTxt)

        init {
            if (editMode && onClickListener != null) {
                val editButton: ImageButton = view.findViewById(R.id.mEditProfileInfoBtn)
                editButton.setOnClickListener { v -> onClickListener!!.iconImageViewOnClick(v, adapterPosition) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileInfoAdapter<T>.MyViewHolder {
        val layout = if (editMode) {
            R.layout.list_profile_info_item
        } else {
            R.layout.list_profile_info_simple_item
        }

        val itemView = LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ProfileInfoAdapterListener {
        fun iconImageViewOnClick(v: View, position: Int)
    }
}