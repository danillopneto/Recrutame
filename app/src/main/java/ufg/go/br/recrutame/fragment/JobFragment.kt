package ufg.go.br.recrutame.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.annotation.NonNull
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import android.view.*
import com.google.firebase.database.*
import ufg.go.br.recrutame.util.Utils
import ufg.go.br.recrutame.model.JobModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ufg.go.br.recrutame.enum.EnumUserIteraction
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindorks.placeholderview.SwipePlaceHolderView
import ufg.go.br.recrutame.Util.GeoLocation
import ufg.go.br.recrutame.model.Match
import ufg.go.br.recrutame.model.Message
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class JobFragment : BaseFragment(){
    private lateinit var database:FirebaseDatabase
    private lateinit var mSwipeView: SwipePlaceHolderView
    private lateinit var acceptBtn: FloatingActionButton
    private lateinit var rejectBtn: FloatingActionButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var boundingCoordinates: Array<GeoLocation>
    private lateinit var mFunctions: FirebaseFunctions
    private lateinit var jobs: Map<Long, JobModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        database = FirebaseDatabase.getInstance()
        mFunctions = FirebaseFunctions.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        val rootView = inflater.inflate(R.layout.fragment_job, container, false)

        mSwipeView = rootView.findViewById(R.id.swipeView) as SwipePlaceHolderView

        val windowSize = Utils.getDisplaySize(activity!!.windowManager)
        val width = Utils.dpToPx(350)
        val height = (windowSize.y / 1.15).toInt()
        mSwipeView.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
                .setDisplayViewCount(2)
                .setHeightSwipeDistFactor(0f)
                .setWidthSwipeDistFactor(5f)
                .setSwipeDecor(SwipeDecor()
                        .setViewHeight(height)
                        .setViewWidth(width)
                        .setPaddingTop(0)
                        .setMarginTop(0)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.card_swipe_in)
                        .setSwipeOutMsgLayoutId(R.layout.card_swipe_out)
                        .setSwipeAnimTime(250))

        getBoundingCoordinates()

        rejectBtn = rootView.findViewById(R.id.rejectBtn)
        rejectBtn.setOnClickListener {
            rejectJob()
        }

        acceptBtn = rootView.findViewById(R.id.acceptBtn)
        acceptBtn.setOnClickListener {
            acceptJob()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUserIteraction(userIteraction: EnumUserIteraction) {
        val handler = Handler()
        handler.postDelayed({
            when (userIteraction) {
                EnumUserIteraction.ACCEPT_JOB -> acceptJob()
                EnumUserIteraction.REJECT_JOB -> rejectJob()
            }

            EventBus.getDefault().removeStickyEvent(EventBus.getDefault().getStickyEvent(EnumUserIteraction::class.java!!))
        }, 300)

    }

    private fun getBoundingCoordinates() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        getJobs(location)
                    }
        }
    }

    private fun getJobs(location :Location?){
        if (location != null) {

            var data = getParamsJobOffer(location)

            mFunctions
                    .getHttpsCallable("getJobOffers")
                    .call(data)
                    .continueWith(object:Continuation<HttpsCallableResult, String> {
                        @Throws(Exception::class)
                        override fun then(@NonNull task:Task<HttpsCallableResult>):String {
                            val result = task.getResult()
                            var data = result?.getData() as String

                            return data
                        }
                    })
                    .addOnCompleteListener(object: OnCompleteListener<String> {
                        override fun onComplete(@NonNull task:Task<String>) {
                            fillCards(task)
                        }
                    });
        }
    }

    private fun fillCards(task:Task<String>){
        if (!task.isSuccessful()) {
            var e = task.getException();
            if (e is FirebaseFunctionsException) {
            }
        } else{
            if(task.result != null){
                var job = Gson().fromJson<List<JobModel>>(task.result, object : TypeToken<List<JobModel>>() {

                }.type)

                jobs = job.associateBy({it.id}, {it})

                for(jobModel in job){
                    mSwipeView.addView(JobCard(context!!, jobModel, mSwipeView, mDatabase, mAuth.currentUser?.uid.orEmpty()))
                }
            }
        }
    }

    private fun getParamsJobOffer(location :Location?):HashMap<String, kotlin.Any>{

        val geoLocation = GeoLocation()
        geoLocation.latitudeInRadians = Math.toRadians(location!!.latitude)
        geoLocation.longitudeInRadians = Math.toRadians(location!!.longitude)

        boundingCoordinates = geoLocation.boundingCoordinates(getMyPreferences().getMaximumDistance().toDouble(), 6371.01)

        val data = HashMap<String, kotlin.Any>()
        data.put("minLatitude", boundingCoordinates[0].latitudeInDegrees)
        data.put("maxLatitude", boundingCoordinates[1].latitudeInDegrees)
        data.put("minLongitude", boundingCoordinates[0].longitudeInDegrees)
        data.put("maxLongitude", boundingCoordinates[1].longitudeInDegrees)
        data.put("userId", mAuth.currentUser?.uid.orEmpty())

        return data
    }

    private fun acceptJob() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        acceptBtn.startAnimation(shake)
        mSwipeView.doSwipe(true)
        var mjobTagIdTxt = mSwipeView.findViewById(R.id.jobTagId) as TextView
    }

    private fun rejectJob() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        rejectBtn.startAnimation(shake)
        mSwipeView.doSwipe(false)
    }
}
