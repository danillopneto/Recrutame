package ufg.go.br.recrutame.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.Log
import com.mindorks.placeholderview.SwipePlaceHolderView
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import android.view.*
import com.google.firebase.database.*
import ufg.go.br.recrutame.util.Utils
import ufg.go.br.recrutame.TAG
import ufg.go.br.recrutame.model.JobModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ufg.go.br.recrutame.enum.EnumUserIteraction
import android.view.animation.AnimationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import ufg.go.br.recrutame.util.GeoLocation

class JobFragment : BaseFragment(){
    private lateinit var database:FirebaseDatabase
    private lateinit var mSwipeView: SwipePlaceHolderView
    private lateinit var acceptBtn: FloatingActionButton
    private lateinit var rejectBtn: FloatingActionButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var boundingCoordinates: Array<GeoLocation>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        database = FirebaseDatabase.getInstance()
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
                        if (location != null) {
                            val geoLocation = GeoLocation()
                            geoLocation.latitudeInRadians = Math.toRadians(location.latitude)
                            geoLocation.longitudeInRadians = Math.toRadians(location.longitude)

                            boundingCoordinates = geoLocation.boundingCoordinates(getMyPreferences().getMaximumDistance().toDouble(), 6371.01)

                            database.getReference("vagas")
                                    .orderByChild("latitude")
                                    .startAt(boundingCoordinates[0].latitudeInDegrees)
                                    .endAt(boundingCoordinates[1].latitudeInDegrees)
                                    .addValueEventListener( object: ValueEventListener{
                                        override fun onDataChange(dataSnapshot: DataSnapshot?) {

                                            for (postSnapshot in dataSnapshot!!.children) {
                                                val jobModel:JobModel? = postSnapshot.getValue(JobModel::class.java)
                                                if (boundingCoordinates[1].longitudeInDegrees >= jobModel!!.longitude
                                                        && boundingCoordinates[0].longitudeInDegrees <= jobModel!!.longitude) {
                                                    jobModel?.key = postSnapshot.key
                                                    mSwipeView.addView(JobCard(context!!, jobModel, mSwipeView))
                                                }
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError?) {
                                            Log.d(TAG, "" + databaseError)
                                        }

                                    })

                        }
                    }
        }
    }

    private fun acceptJob() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        acceptBtn.startAnimation(shake)
        mSwipeView.doSwipe(true)
    }

    private fun rejectJob() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        rejectBtn.startAnimation(shake)
        mSwipeView.doSwipe(false)
    }
}
