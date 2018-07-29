package ufg.go.br.recrutame.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ufg.go.br.recrutame.R
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat.getColor


class UserPhotoFragment : BaseFragment(), View.OnClickListener, ISlideBackgroundColorHolder {
    private lateinit var mProfileImage: CircleImageView
    private lateinit var mLayoutContainer: ViewGroup

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        val view = inflater.inflate(R.layout.fragment_user_photo, container, false)
        inicializeControls(view, container!!)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mChangePictureBtn -> chooseImage()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImageEvent(image: Uri?) {
        Picasso.get().load(image).into(mProfileImage)
    }

    override fun getDefaultBackgroundColor(): Int {
        // Return the default background color of the slide.
        return getColor(context?.applicationContext!!, R.color.pink_800)
    }

    override fun setBackgroundColor(@ColorInt backgroundColor: Int) {
        // Set the background color of the view within your slide to which the transition should be applied.
        mLayoutContainer.setBackgroundColor(backgroundColor)
    }

    private fun inicializeControls(view: View, container: ViewGroup) {
        mProfileImage = view.findViewById(R.id.mProfileImage)
        mLayoutContainer = container
        view.findViewById<FloatingActionButton>(R.id.mChangePictureBtn)?.setOnClickListener(this)
    }
}