package ufg.go.br.recrutame.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserExperienceInfo
import ufg.go.br.recrutame.util.Utils

class ExperienceSimpleAdapter(private var experiences: List<UserExperienceInfo>, private var activity: Activity)
    : RecyclerView.Adapter<ExperienceSimpleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var experienceTitle: TextView = view.findViewById(R.id.mExperienceTitleTxt)
        var company: TextView = view.findViewById(R.id.mCompanyTxt)
        var period: TextView = view.findViewById(R.id.mPeriodTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceSimpleAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_experience_simple, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExperienceSimpleAdapter.MyViewHolder, position: Int) {
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
}