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
import ufg.go.br.recrutame.adapter.ExperienceAdapter
import ufg.go.br.recrutame.model.UserExperienceInfo

class EditExperiencesInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mExperienceInfoLayout
    private var experiences = mutableListOf<UserExperienceInfo>()
    private lateinit var mExperiencesRv: RecyclerView
    private lateinit var mExperienceAdapter: ExperienceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_experiences_info)
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
        val i = Intent(this, AddEditExperienceInfoActivity:: class.java)
        i.putExtra("userId", userId)
        i.putExtra("experienceKey", "")
        startActivity(i)
    }

    override fun getActionMenu(): Int {
        return R.menu.menu_action_edit_add
    }

    override fun getActionBarTitle(): String {
        return getString(R.string.experience)
    }

    private fun inicializeControls() {
        mDatabase.child("users/$userId/experiences")
                .orderByChild("language")
                .addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        experiences.clear()
                        for (child in dataSnapshot!!.children) {
                            val experience = child.getValue(UserExperienceInfo::class.java)
                            if (experience != null) {
                                experiences.add(experience)
                            }
                        }

                        inflateExperienceAdapter(experiences)
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        Log.d(ufg.go.br.recrutame.util.TAG, "" + databaseError)
                    }
                })
    }

    private fun inflateExperienceAdapter(experiences: List<UserExperienceInfo>) {
        mExperiencesRv = findViewById(R.id.mExperiencesRv)

        val experiencesOrdered = experiences.sortedByDescending { it.startDate }
        mExperienceAdapter = ExperienceAdapter(experiencesOrdered, this, true, object : ExperienceAdapter.ExperienceAdapterListener {
            override fun iconImageViewOnClick(v: View, position: Int) {
                val i = Intent(baseContext,  AddEditExperienceInfoActivity:: class.java)
                i.putExtra("userId", userId)
                i.putExtra("experienceKey", experiencesOrdered[position].key)
                i.putExtra("experienceTitle", experiencesOrdered[position].title)
                i.putExtra("experienceCompany", experiencesOrdered[position].company)
                i.putExtra("experienceStartDate", experiencesOrdered[position].startDate)
                i.putExtra("experienceEndDate", experiencesOrdered[position].endDate)
                startActivity(i)
            }
        })

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mExperiencesRv.layoutManager = mLayoutManager
        mExperiencesRv.itemAnimator = DefaultItemAnimator()
        mExperiencesRv.adapter = mExperienceAdapter

        mExperienceAdapter.notifyDataSetChanged()
    }
}
