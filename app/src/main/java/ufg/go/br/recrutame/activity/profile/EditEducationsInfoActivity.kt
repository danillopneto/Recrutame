package ufg.go.br.recrutame.activity.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.adapter.EducationAdapter
import ufg.go.br.recrutame.adapter.ProfileInfoAdapter
import ufg.go.br.recrutame.model.UserEducationInfo

class EditEducationsInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mProfileInfoLayout
    private var educations = mutableListOf<UserEducationInfo>()
    private lateinit var mEducationsRv: RecyclerView
    private lateinit var mEducationsAdapter: EducationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_info)
        inicializeControls()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish(); return true; }
            R.id.mMenuAddItem -> saveInfo()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun saveInfo() {
        val i = Intent(this, AddEditEducationInfoActivity:: class.java)
        i.putExtra("userId", userId)
        i.putExtra("educationKey", "")
        startActivity(i)
    }

    override fun getActionMenu(): Int {
        return R.menu.menu_action_edit_add
    }

    override fun getActionBarTitle(): String {
        return getString(R.string.education)
    }

    private fun inicializeControls() {
        mDatabase.child("users/$userId/educations")
                .addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        educations.clear()
                        for (child in dataSnapshot!!.children) {
                            val education = child.getValue(UserEducationInfo::class.java)
                            if (education != null) {
                                educations.add(education)
                            }
                        }

                        inflateEducationsAdapter(educations)
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        Log.d(ufg.go.br.recrutame.util.TAG, "" + databaseError)
                    }
                })
    }

    private fun inflateEducationsAdapter(educations: List<UserEducationInfo>) {
        mEducationsRv = findViewById(R.id.mProfileInfoRv)

        val educationsOrdered = educations.sortedByDescending { it.startDate }
        mEducationsAdapter = EducationAdapter(educationsOrdered, this, true, object : ProfileInfoAdapter.ProfileInfoAdapterListener {
            override fun iconImageViewOnClick(v: View, position: Int) {
                val i = Intent(baseContext,  AddEditEducationInfoActivity:: class.java)
                i.putExtra("userId", userId)
                i.putExtra("educationKey", educationsOrdered[position].key)
                i.putExtra("educationSchool", educationsOrdered[position].school)
                i.putExtra("educationDegree", educationsOrdered[position].degree)
                i.putExtra("educationStartDate", educationsOrdered[position].startDate)
                i.putExtra("educationEndDate", educationsOrdered[position].endDate)
                startActivity(i)
            }
        })

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mEducationsRv.layoutManager = mLayoutManager
        mEducationsRv.itemAnimator = DefaultItemAnimator()
        mEducationsRv.adapter = mEducationsAdapter

        mEducationsAdapter.notifyDataSetChanged()
    }
}
