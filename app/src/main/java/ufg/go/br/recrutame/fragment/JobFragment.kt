package ufg.go.br.recrutame.fragment

import android.os.Bundle
import android.util.Log
import com.mindorks.placeholderview.SwipePlaceHolderView
import ufg.go.br.recrutame.JobCard
import ufg.go.br.recrutame.R
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import android.view.*
import com.google.firebase.database.*
import ufg.go.br.recrutame.Util.Utils
import com.sackcentury.shinebuttonlib.ShineButton
import ufg.go.br.recrutame.TAG
import ufg.go.br.recrutame.model.JobModel
import com.github.florent37.tutoshowcase.TutoShowcase
import org.greenrobot.eventbus.EventBus
import ufg.go.br.recrutame.CLIENT_ID
import ufg.go.br.recrutame.enum.EnumShowCase
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig


class JobFragment : BaseFragment(){
    private lateinit var database:FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        database = FirebaseDatabase.getInstance();

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

        database.getReference("vagas").addValueEventListener( object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot?) {

                for (postSnapshot in dataSnapshot!!.children) {
                    var jobModel:JobModel? = postSnapshot.getValue(JobModel::class.java)
                    jobModel?.key = postSnapshot.key
                    mSwipeView.addView(JobCard(rootView.context, jobModel, mSwipeView))
                }
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.d(TAG, "" + databaseError);
            }

        })

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
