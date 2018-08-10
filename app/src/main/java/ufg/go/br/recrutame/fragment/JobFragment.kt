package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.util.Log
import com.mindorks.placeholderview.SwipePlaceHolderView
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import android.view.*
import com.google.firebase.database.*
import ufg.go.br.recrutame.Util.Utils
import ufg.go.br.recrutame.TAG
import ufg.go.br.recrutame.model.JobModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ufg.go.br.recrutame.enum.EnumUserIteraction
import android.view.animation.AnimationUtils



class JobFragment : BaseFragment(){
    private lateinit var database:FirebaseDatabase
    private lateinit var mSwipeView: SwipePlaceHolderView
    private lateinit var acceptBtn: FloatingActionButton
    private lateinit var rejectBtn: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        database = FirebaseDatabase.getInstance()

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
                        .setSwipeOutMsgLayoutId(R.layout.card_swipe_out))

        database.getReference("vagas").addValueEventListener( object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot?) {

                for (postSnapshot in dataSnapshot!!.children) {
                    val jobModel:JobModel? = postSnapshot.getValue(JobModel::class.java)
                    jobModel?.key = postSnapshot.key
                    mSwipeView.addView(JobCard(context!!, jobModel, mSwipeView))
                }
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.d(TAG, "" + databaseError)
            }

        })

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
