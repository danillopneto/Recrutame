package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ImageButton
import com.mindorks.placeholderview.SwipePlaceHolderView
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.JobModel
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import ufg.go.br.recrutame.Util.JobUtils
import android.R.attr.y
import android.graphics.Path
import android.media.Image
import android.support.constraint.Constraints.TAG
import android.util.Log
import android.view.*
import ufg.go.br.recrutame.Util.Utils
import android.view.GestureDetector
import android.widget.Button
import android.content.Intent
import com.sackcentury.shinebuttonlib.ShineButton
import ufg.go.br.recrutame.JobDetailActivity


class JobFragment : BaseFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        var rootView = inflater.inflate(R.layout.fragment_job, container, false);

        var mSwipeView = rootView.findViewById(R.id.swipeView) as SwipePlaceHolderView;

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

        for (job in JobUtils.getJobs()){
            mSwipeView.addView(JobCard(rootView.context, job, mSwipeView))
        }

        var rejectBtn: ShineButton = rootView.findViewById(R.id.rejectBtn)
        rejectBtn.setOnClickListener {
            mSwipeView.doSwipe(false)
            rejectBtn.isChecked = false
        }


        var acceptBtn: ShineButton = rootView.findViewById(R.id.acceptBtn)
        acceptBtn.setOnClickListener {
            mSwipeView.doSwipe(true)
            acceptBtn.isChecked = false
        }

        return rootView
    }
}
