package ufg.go.br.recrutame.adapter

import android.app.Activity
import ufg.go.br.recrutame.model.UserExperienceInfo
import ufg.go.br.recrutame.util.Utils

class ExperienceAdapter(
                        private var experiences: List<UserExperienceInfo>,
                        private var activity: Activity,
                        editMode: Boolean,
                        onClickListener: ProfileInfoAdapter.ProfileInfoAdapterListener?)
    : ProfileInfoAdapter<UserExperienceInfo>(experiences, editMode, onClickListener) {

    override fun onBindViewHolder(holder: ProfileInfoAdapter<UserExperienceInfo>.MyViewHolder, position: Int) {
        val item = experiences[position]
        holder.title.text = item.title
        holder.subtitle.text = item.company
        holder.period.text = Utils.getFromToAsText(item.startDate!!, item.endDate, activity, true).toString()
    }
}