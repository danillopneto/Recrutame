package ufg.go.br.recrutame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import ufg.go.br.recrutame.enum.EnumUserIteraction
import ufg.go.br.recrutame.model.JobModel
import java.text.NumberFormat
import java.util.*

class JobDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_job)
        inicializeControls()

        val codigoVaga = intent.getStringExtra("key")

        database = FirebaseDatabase.getInstance()
        database.getReference("vagas").child(codigoVaga).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var jobTitleTxt: TextView = findViewById(R.id.jobTitleTxt)
                var jobCompanyTxt: TextView = findViewById(R.id.jobCompanyTxt)
                var jobImageView: ImageView = findViewById(R.id.jobImg)
                var jobDescriptionTxt: TextView = findViewById(R.id.jobDescriptionTxt)
                var jobTypeTxt: TextView = findViewById(R.id.jobTypeTxt)
                var jobLocationTxt: TextView = findViewById(R.id.jobLocationTxt)
                var jobSalaryTxt: TextView = findViewById(R.id.jobSalaryTxt)

                var jobModel: JobModel? = dataSnapshot.getValue(JobModel::class.java)
                jobTitleTxt.text = jobModel?.title
                jobCompanyTxt.text = jobModel?.company
                jobDescriptionTxt.text = jobModel?.description
                jobLocationTxt.text = jobModel?.city + ", " + jobModel?.state + " - " + jobModel?.country
                jobTypeTxt.text = jobModel?.type

                val preferences = getMyPreferences()
                val format = NumberFormat.getCurrencyInstance(Locale(preferences.getLanguage(), preferences.getCountry()))

                jobSalaryTxt.text = format.format(jobModel?.salary)
                Picasso.get().load(jobModel?.image).into(jobImageView)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })

        /*val jobTitle = intent.getStringExtra("title")
        val jobCompany = intent.getStringExtra("company")
        val jobImage = intent.getStringExtra("image")

        var jobTitleTxt: TextView = findViewById(R.id.jobTitleTxt)
        var jobCompanyTxt: TextView = findViewById(R.id.jobCompanyTxt)
        var jobImageView: ImageView = findViewById(R.id.jobImg)

        jobTitleTxt.setText(jobTitle)
        jobCompanyTxt.setText(jobCompany)
        Picasso.get().load(jobImage).into(jobImageView);*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rejectBtn -> rejectJob()
            R.id.acceptBtn -> acceptJob()
        }
    }

    private fun inicializeControls() {
        findViewById<FloatingActionButton>(R.id.rejectBtn).setOnClickListener(this)
        findViewById<FloatingActionButton>(R.id.acceptBtn).setOnClickListener(this)
    }

    private fun acceptJob() {
        EventBus.getDefault().postSticky(EnumUserIteraction.ACCEPT_JOB)
        finish()
    }

    private fun rejectJob() {
        EventBus.getDefault().postSticky(EnumUserIteraction.REJECT_JOB)
        finish()
    }
}