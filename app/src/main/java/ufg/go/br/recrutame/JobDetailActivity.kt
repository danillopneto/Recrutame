package ufg.go.br.recrutame

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class JobDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var database: FirebaseDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var jobDistanceContainer: LinearLayout
    private lateinit var jobDistanceTxt: TextView
    private val jobLocation: Location = Location("jobLocation")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_job)
        inicializeControls()

        val codigoVaga = intent.getStringExtra("key")

        database = FirebaseDatabase.getInstance()
        database.getReference("vagas").child(codigoVaga).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val jobTitleTxt: TextView = findViewById(R.id.jobTitleTxt)
                val jobCompanyTxt: TextView = findViewById(R.id.jobCompanyTxt)
                val jobImageView: ImageView = findViewById(R.id.jobImg)
                val jobDescriptionTxt: TextView = findViewById(R.id.jobDescriptionTxt)
                val jobTypeTxt: TextView = findViewById(R.id.jobTypeTxt)
                val jobLocationTxt: TextView = findViewById(R.id.jobLocationTxt)
                jobDistanceContainer = findViewById(R.id.jobDistanceContainer)
                jobDistanceTxt = findViewById(R.id.jobDistanceTxt)
                val jobSalaryTxt: TextView = findViewById(R.id.jobSalaryTxt)

                val jobModel: JobModel? = dataSnapshot.getValue(JobModel::class.java)
                jobTitleTxt.text = jobModel?.title
                jobCompanyTxt.text = jobModel?.company
                jobDescriptionTxt.text = jobModel?.description
                jobLocationTxt.text = "${jobModel?.city}, ${jobModel?.state} - ${jobModel?.country}"
                jobTypeTxt.text = jobModel?.type

                jobLocation.latitude = jobModel?.latitude!!
                jobLocation.longitude = jobModel.longitude!!
                handleDistance()

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

    private fun handleDistance() {
        if (jobLocation.latitude == 0.0
                && jobLocation.longitude == 0.0) {
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if (location != null) {
                            val distance = location.distanceTo(jobLocation)
                            val kilometers = (distance/1000).toInt()
                            if (kilometers < 1) {
                                jobDistanceTxt.text = getString(R.string.less_than_one_km)
                            } else {
                                jobDistanceTxt.text = "${kilometers}Km"
                            }

                            jobDistanceContainer.visibility = View.VISIBLE
                        }
                    }
        }
    }
}