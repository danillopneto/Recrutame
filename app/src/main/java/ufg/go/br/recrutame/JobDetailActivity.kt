package ufg.go.br.recrutame

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class JobDetailActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_job)

        val jobTitle = intent.getStringExtra("title")
        val jobCompany = intent.getStringExtra("company")
        val jobImage = intent.getStringExtra("image")

        var jobTitleTxt: TextView = findViewById(R.id.jobTitleTxt)
        var jobCompanyTxt: TextView = findViewById(R.id.jobCompanyTxt)
        var jobImageView: ImageView = findViewById(R.id.jobImg)

        jobTitleTxt.setText(jobTitle)
        jobCompanyTxt.setText(jobCompany)
        Picasso.get().load(jobImage).into(jobImageView);
    }
}