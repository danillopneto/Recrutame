package ufg.go.br.recrutame

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState
import com.mindorks.placeholderview.annotations.swipe.SwipeInState
import com.mindorks.placeholderview.annotations.swipe.SwipeIn
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState
import com.mindorks.placeholderview.annotations.swipe.SwipeOut
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.squareup.picasso.Picasso
import ufg.go.br.recrutame.model.JobModel
import android.content.Context
import android.content.Intent
import com.google.firebase.database.DatabaseReference
import ufg.go.br.recrutame.enum.EnumStatusProcessoSeletivo
import ufg.go.br.recrutame.model.Match
import ufg.go.br.recrutame.model.Message
import ufg.go.br.recrutame.util.Utils
import java.text.SimpleDateFormat
import java.util.*

@Layout(R.layout.card_view_job)
class JobCard(
        private val mContext: Context,
        private val mJob: JobModel?,
        private val mSwipeView: SwipePlaceHolderView,
        private var mDatabase: DatabaseReference,
        private var userId: String) {

    @View(R.id.jobImg)
    private val jobImg: ImageView? = null

    @View(R.id.jobTitleTxt)
    private val jobTitleTxt: TextView? = null

    @View(R.id.jobCompanyTxt)
    private val jobCompanyTxt: TextView? = null

    @View(R.id.jobInfo)
    private val jobInfoBtn: ImageButton? = null

    @View(R.id.jobTagId)
    private val jobTagId: TextView? = null

    @Resolve
    private fun onResolved() {

        Picasso.get().load(mJob?.image).into(jobImg)
        jobTitleTxt!!.setText(mJob?.title)
        jobCompanyTxt!!.setText(mJob?.company)
        jobTagId!!.setText(mJob?.id.toString())
        jobInfoBtn!!.setOnClickListener({
            val intent = Intent(mContext, JobDetailActivity::class.java)
            intent.putExtra("id", mJob?.id)
            mContext.startActivity(intent)
        })
    }

    @SwipeOut
    private fun onSwipedOut() {
        Log.d("EVENT", "onSwipedOut")
        mSwipeView.addView(this)
    }

    @SwipeCancelState
    private fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
    }

    @SwipeIn
    private fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
        addMatch()
    }

    @SwipeInState
    private fun onSwipeInState() {
        Log.d("EVENT", "onSwipeInState")
    }

    @SwipeOutState
    private fun onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState")
    }

    private fun addMatch(){
        var userId = userId
        var jobId = mJob?.id!!
        var dateApplied = Utils.getCurrentDate()
        var match = Match(dateApplied, mJob.image, mJob.company!!, jobId, EnumStatusProcessoSeletivo.PENDING, ArrayList<Message>() )

        var matchesRef = mDatabase.child("matches/$userId")
        matchesRef.child(jobId.toString()).setValue(match)
    }
}