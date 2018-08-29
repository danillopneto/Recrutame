package ufg.go.br.recrutame.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserExperienceInfo
import ufg.go.br.recrutame.util.Utils

class ExperienceAdapter(
                        private var experiences: List<UserExperienceInfo>,
                        private var activity: Activity,
                        private var editMode: Boolean,
                        private var onClickListener: ExperienceAdapterListener?)
    : RecyclerView.Adapter<ExperienceAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var experienceTitle: TextView = view.findViewById(R.id.mExperienceTitleTxt)
        var company: TextView = view.findViewById(R.id.mExperienceCompanyTxt)
        var period: TextView = view.findViewById(R.id.mExperiencePeriodTxt)

        init {
            if (editMode && onClickListener != null) {
                val editButton: ImageButton = view.findViewById(R.id.mEditLanguagueBtn)
                editButton.setOnClickListener { v -> onClickListener!!.iconImageViewOnClick(v, adapterPosition) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceAdapter.MyViewHolder {
        val layout = if (editMode) {
            R.layout.list_experience_item
        } else {
            R.layout.list_experience_simple_item
        }

        val itemView = LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExperienceAdapter.MyViewHolder, position: Int) {
        val item = experiences[position]
        holder.experienceTitle.text = item.title
        holder.company.text = item.company

        val periodText = StringBuilder()
        periodText.append(Utils.getFormatedDate(item.startDate, activity.getString(R.string.format_date_no_day)))
        periodText.append(" - ")
        if (item.endDate != null){
            periodText.append(Utils.getFormatedDate(item.endDate, activity.getString(R.string.format_date_no_day)))
        } else {
            periodText.append(activity.getString(R.string.present))
        }

        periodText.append(" ◔ ")

        val calculatedPeriod = Utils.calculatePeriod(item.startDate!!, item.endDate)
        periodText.append(calculatedPeriod.years)
        periodText.append(" ")
        periodText.append(activity.getString(R.string.years))
        if (calculatedPeriod.months > 0) {
            periodText.append(" ")
            periodText.append(calculatedPeriod.months)
            periodText.append(" ")
            periodText.append(activity.getString(R.string.months))
        }

        holder.period.text = periodText.toString()
    }

    override fun getItemCount(): Int {
        return experiences.size
    }

    interface ExperienceAdapterListener {
        fun iconImageViewOnClick(v: View, position: Int)
    }
}