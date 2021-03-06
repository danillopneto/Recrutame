package ufg.go.br.recrutame.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.annotations.NotNull
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.activity.profile.*
import ufg.go.br.recrutame.adapter.EducationAdapter
import ufg.go.br.recrutame.adapter.ExperienceAdapter
import ufg.go.br.recrutame.adapter.LanguageAdapter
import ufg.go.br.recrutame.util.TAG
import ufg.go.br.recrutame.util.Utils
import ufg.go.br.recrutame.model.*

class ProfileFragment : BaseFragment(), View.OnClickListener  {
    private lateinit var database: FirebaseDatabase
    private lateinit var mProfileImage: CircleImageView
    private lateinit var mExperiencesRv: RecyclerView
    private lateinit var mExperienceAdapter: ExperienceAdapter
    private lateinit var mEducationRv: RecyclerView
    private lateinit var mEducationAdapter: EducationAdapter
    private lateinit var mLanguagesRv: RecyclerView
    private lateinit var mLanguageAdapter: LanguageAdapter

    private var userModel: UserProfile? = null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImageEvent(image: Uri?) {
        Picasso.get().load(image).into(mProfileImage)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inicializeApis()
        database = FirebaseDatabase.getInstance()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        database.getReference("users/" + mAuth.currentUser?.uid)
                .addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(@NotNull dataSnapshot: DataSnapshot) {
                        userModel = dataSnapshot.getValue(UserProfile::class.java)
                        if (userModel != null) {
                            fillGeneralInfo(view, userModel!!.generalInfo)
                            fillContactInfo(view, userModel!!.contactInfo)
                            inflateExperienceAdapter(userModel!!.experiences)
                            inflateEducationAdapter(userModel!!.educations)
                            inflateLanguagesAdapter(userModel!!.languages)
                        }
                    }

                    override fun onCancelled(@NotNull databaseError: DatabaseError) {
                        Log.d(TAG, "" + databaseError)
                    }
                })

        inicializeControls(view)
        return view
    }

    private fun inicializeControls(view: View) {
        mProfileImage = view.findViewById(R.id.mProfileImage)

        view.findViewById<FloatingActionButton>(R.id.mChangePictureBtn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.mEditGeneralInfoBtn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.mEditContactInfoBtn).setOnClickListener(this)

        view.findViewById<ImageButton>(R.id.mEditExperincesInfoBtn).setOnClickListener(this)
        mExperiencesRv = view.findViewById(R.id.mExperiencesRv)

        view.findViewById<ImageButton>(R.id.mEditEducationsInfoBtn).setOnClickListener(this)
        mEducationRv = view.findViewById(R.id.mEducationRv)

        view.findViewById<ImageButton>(R.id.mEditLanguaguesInfoBtn).setOnClickListener(this)
        mLanguagesRv = view.findViewById(R.id.mLanguagesRv)

        mStorageRef.child(getUserPhotoUrl()).downloadUrl.addOnSuccessListener { task ->
            EventBus.getDefault().post(task)
        }.addOnFailureListener{
            EventBus.getDefault().post(R.drawable.user_logo)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mChangePictureBtn -> chooseImage()
            R.id.mEditGeneralInfoBtn -> editGeneralInfo()
            R.id.mEditExperincesInfoBtn -> editExperiencesInfo()
            R.id.mEditEducationsInfoBtn -> editEducationsInfo()
            R.id.mEditContactInfoBtn -> editContactInfo()
            R.id.mEditLanguaguesInfoBtn -> editLanguagesInfo()
        }
    }

    private fun editContactInfo() {
        val i = Intent(context, EditContactInfoActivity:: class.java)
        i.putExtra("userId", mAuth.currentUser?.uid)
        i.putExtra("userEmail", userModel?.contactInfo?.email)
        i.putExtra("userWebsite", userModel?.contactInfo?.webSite)
        i.putExtra("userPhone", userModel?.contactInfo?.phone)
        startActivity(i)
    }

    private fun editEducationsInfo() {
        val i = Intent(context, EditEducationsInfoActivity:: class.java)
        i.putExtra("userId", mAuth.currentUser?.uid)
        startActivity(i)
    }

    private fun editExperiencesInfo() {
        val i = Intent(context, EditExperiencesInfoActivity:: class.java)
        i.putExtra("userId", mAuth.currentUser?.uid)
        startActivity(i)
    }

    private fun editGeneralInfo() {
        val i = Intent(context, EditGeneralInfoActivity:: class.java)
        i.putExtra("userId", mAuth.currentUser?.uid)
        i.putExtra("userName", userModel?.generalInfo?.name)
        i.putExtra("userLastName", userModel?.generalInfo?.lastName)
        i.putExtra("userBirthdate", userModel?.generalInfo?.birthdate)
        i.putExtra("userGender", userModel?.generalInfo?.gender)
        i.putExtra("userState", userModel?.generalInfo?.state)
        i.putExtra("userCity", userModel?.generalInfo?.city)
        startActivity(i)
    }

    private fun editLanguagesInfo() {
        val i = Intent(context, EditLanguagesInfoActivity:: class.java)
        i.putExtra("userId", mAuth.currentUser?.uid)
        startActivity(i)
    }

    private fun fillContactInfo(view: View, contactInfo: UserContactInfo) {
        view.findViewById<TextView>(R.id.mEmailTxt).text = contactInfo.email
        if (!contactInfo.webSite.isEmpty()) {
            val websiteTxt = view.findViewById<TextView>(R.id.mWebsiteTxt)
            websiteTxt.text = contactInfo.webSite
            websiteTxt.visibility = View.VISIBLE
        }

        if (!contactInfo.phone.isEmpty()) {
            val phoneTxt = view.findViewById<TextView>(R.id.mPhoneTxt)
            phoneTxt.text = contactInfo.phone
            phoneTxt.visibility = View.VISIBLE
        }
    }

    private fun fillGeneralInfo(view: View, generalInfo: UserGeneralInfo) {
        view.findViewById<TextView>(R.id.mUsernameTxt).text = "${generalInfo.name} ${generalInfo.lastName}"

        val generalInfoTxt = view.findViewById<TextView>(R.id.mGeneralInfoTxt)
        val generalInfoData = StringBuilder()
        if (!generalInfo.gender.isEmpty()) {
            generalInfoData.appendln(generalInfo.gender)
        }

        if (generalInfo.birthdate != null
                && activity != null) {
            generalInfoData.append(Utils.getFormatedDate(generalInfo.birthdate, getString(R.string.format_date_full)))
            generalInfoData.append(" ◔ ")
            generalInfoData.append(Utils.calculatePeriod(generalInfo.birthdate, null).years)
            generalInfoData.append(" ")
            generalInfoData.appendln(getString(R.string.years))
        }

        generalInfoData.appendln("${generalInfo.city} - ${generalInfo.state}")

        generalInfoTxt.text = generalInfoData.toString()
    }

    private fun inflateLanguagesAdapter(languages: HashMap<String, UserLanguageInfo>) {
        if (languages.isEmpty()
                || activity == null) {
            return
        }

        val languagesAsList = languages.values.sortedBy { it.language }
        mLanguageAdapter = LanguageAdapter(languagesAsList, activity!!, false, null)

        val mLayoutManager = LinearLayoutManager(context)
        mLanguagesRv.layoutManager = mLayoutManager
        mLanguagesRv.itemAnimator = DefaultItemAnimator()
        mLanguagesRv.adapter = mLanguageAdapter

        mLanguageAdapter.notifyDataSetChanged()
    }

    private fun inflateEducationAdapter(educations: HashMap<String, UserEducationInfo>) {
        if (educations.isEmpty()
                || activity == null) {
            return
        }

        val educationsAsList = educations.values.sortedByDescending { it.startDate }
        mEducationAdapter = EducationAdapter(educationsAsList, activity!!, false, null)

        val mLayoutManager = LinearLayoutManager(context)
        mEducationRv.layoutManager = mLayoutManager
        mEducationRv.itemAnimator = DefaultItemAnimator()
        mEducationRv.adapter = mEducationAdapter

        mExperienceAdapter.notifyDataSetChanged()
    }

    private fun inflateExperienceAdapter(experiences: HashMap<String, UserExperienceInfo>) {
        if (experiences.isEmpty()
                || activity == null) {
            return
        }

        val experiencesAsList = experiences.values.sortedByDescending { it.startDate }
        mExperienceAdapter = ExperienceAdapter(experiencesAsList, activity!!, false, null)

        val mLayoutManager = LinearLayoutManager(context)
        mExperiencesRv.layoutManager = mLayoutManager
        mExperiencesRv.itemAnimator = DefaultItemAnimator()
        mExperiencesRv.adapter = mExperienceAdapter

        mExperienceAdapter.notifyDataSetChanged()
    }
}