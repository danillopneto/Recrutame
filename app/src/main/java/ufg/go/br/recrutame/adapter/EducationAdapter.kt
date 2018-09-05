package ufg.go.br.recrutame.adapter

import android.app.Activity
import ufg.go.br.recrutame.model.UserEducationInfo
import ufg.go.br.recrutame.util.Utils

class EducationAdapter(
                        private var educations: List<UserEducationInfo>,
                        private var activity: Activity,
                        editMode: Boolean,
                        onClickListener: ProfileInfoAdapter.ProfileInfoAdapterListener?)
    : ProfileInfoAdapter<UserEducationInfo>(educations, editMode, onClickListener) {

    override fun onBindViewHolder(holder: ProfileInfoAdapter<UserEducationInfo>.MyViewHolder, position: Int) {
        val item = educations[position]
        holder.title.text = item.degree
        holder.subtitle.text = item.school
        holder.period.text = Utils.getFromToAsText(item.startDate!!, item.endDate, activity, false).toString()
    }
}