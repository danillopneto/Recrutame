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

@Layout(R.layout.card_view_job)
class JobCard(private val mContext: Context, private val mJob: JobModel?, private val mSwipeView: SwipePlaceHolderView) {

    @View(R.id.jobImg)
    private val jobImg: ImageView? = null

    @View(R.id.jobTitleTxt)
    private val jobTitleTxt: TextView? = null

    @View(R.id.jobCompanyTxt)
    private val jobCompanyTxt: TextView? = null

    @View(R.id.jobInfo)
    private val jobInfoBtn: ImageButton? = null

    @Resolve
    private fun onResolved() {

        Picasso.get().load(mJob?.image).into(jobImg);
        jobTitleTxt!!.setText(mJob?.title)
        jobCompanyTxt!!.setText(mJob?.company)
        jobInfoBtn!!.setOnClickListener({
            val intent = Intent(mContext, JobDetailActivity::class.java)
            intent.putExtra("key", mJob?.key)
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
        Log.d("EVENT", "onSwipedIn")
    }

    @SwipeInState
    private fun onSwipeInState() {
        Log.d("EVENT", "onSwipeInState")
    }

    @SwipeOutState
    private fun onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState")
    }
}