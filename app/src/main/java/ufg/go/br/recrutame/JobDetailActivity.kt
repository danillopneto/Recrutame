package ufg.go.br.recrutame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import ufg.go.br.recrutame.model.JobModel

class JobDetailActivity : AppCompatActivity(){
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_job)

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
                jobTitleTxt.setText(jobModel?.title)
                jobCompanyTxt.setText(jobModel?.company)
                jobDescriptionTxt.setText(jobModel?.description)
                jobLocationTxt.setText(jobModel?.city + ", " + jobModel?.state + " - " + jobModel?.country)
                jobTypeTxt.setText(jobModel?.type)
                jobSalaryTxt.setText(jobModel?.salary.toString())
                Picasso.get().load(jobModel?.image).into(jobImageView);
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
}