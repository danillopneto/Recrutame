package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserExperienceInfo
import ufg.go.br.recrutame.util.Utils

class AddEditExperienceInfoActivity : EditProfileActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    override var layoutId: Int = R.id.mAddEditExperienceContainer

    private lateinit var mExperienceTitleTxt: EditText
    private lateinit var mExperienceCompanyTxt: EditText
    private lateinit var mStartDateTxt: EditText
    private lateinit var mEndDateContainer: TextInputLayout
    private lateinit var mEndDateTxt: EditText
    private lateinit var mCurrentWorkChk: CheckBox
    private lateinit var mRemoveExperienceBtn: Button
    private var experienceKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        experienceKey = intent.getStringExtra("experienceKey")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_experience_info)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mStartDateTxt -> handleDatePicker(mStartDateTxt)
            R.id.mEndDateTxt -> handleDatePicker(mEndDateTxt)
            R.id.mCurrentWorkChk -> handleCurrentWork()
            R.id.mRemoveExperienceBtn -> {
                handleYesNoDialog(R.string.really_remove_experience) { removeExperience() }
            }
        }
    }

    override fun getActionBarTitle(): String {
        return if (experienceKey.isEmpty()) {
            getString(R.string.add_experience)
        } else {
            getString(R.string.edit_experience)
        }
    }

    override fun inicializeControls() {
        infoReference = mDatabase.child("users/$userId/experiences")

        mExperienceTitleTxt = findViewById(R.id.mExperienceTitleTxt)
        mExperienceCompanyTxt = findViewById(R.id.mExperienceCompanyTxt)

        mStartDateTxt = findViewById(R.id.mStartDateTxt)
        mStartDateTxt.setOnClickListener(this)

        mEndDateContainer = findViewById(R.id.mEndDateContainer)
        mEndDateTxt = findViewById(R.id.mEndDateTxt)
        mEndDateTxt.setOnClickListener(this)

        mCurrentWorkChk = findViewById(R.id.mCurrentWorkChk)
        mCurrentWorkChk.setOnClickListener(this)

        if (!experienceKey.isEmpty()) {
            mExperienceTitleTxt.setText(intent.getStringExtra("title"))
            mExperienceCompanyTxt.setText(intent.getStringExtra("experienceCompany"))
            val startDate = intent.getIntExtra("experienceStartDate", 0)
            if (startDate > 0) {
                mStartDateTxt.setText(Utils.getFormatedDate(startDate, getString(R.string.format_date_no_day)))
            }

            val endDate = intent.getIntExtra("experienceEndDate", 0)
            if (endDate > 0) {
                mEndDateTxt.setText(Utils.getFormatedDate(endDate, getString(R.string.format_date_no_day)))
            } else {
                mCurrentWorkChk.isChecked = true
            }

            mRemoveExperienceBtn = findViewById(R.id.mRemoveExperienceBtn)
            mRemoveExperienceBtn.setOnClickListener(this)
            mRemoveExperienceBtn.visibility = View.VISIBLE
        }

        handleCurrentWork()
    }

    override fun saveInfo() {
        hideKeyboard()
        if (!validateForm()) {
            return
        }

        if (experienceKey.isEmpty()) {
            val newExperience = infoReference.push()
            val data = UserExperienceInfo(
                    newExperience.key,
                    mExperienceTitleTxt.text.toString(),
                    mExperienceCompanyTxt.text.toString(),
                    Utils.getFullDateFromMonthYear(mStartDateTxt.text.toString()),
                    Utils.getFullDateFromMonthYear(mEndDateTxt.text.toString()))
            newExperience.setValue(data).addOnCompleteListener {
                finish()
            }.addOnFailureListener {
                showError(R.string.add_language_info_error)
            }
        } else {
            val data = UserExperienceInfo(
                    experienceKey,
                    mExperienceTitleTxt.text.toString(),
                    mExperienceCompanyTxt.text.toString(),
                    Utils.getFullDateFromMonthYear(mStartDateTxt.text.toString()),
                    Utils.getFullDateFromMonthYear(mEndDateTxt.text.toString()))
            infoReference.child(experienceKey).setValue(data).addOnCompleteListener {
                finish()
            }.addOnFailureListener {
                showError(R.string.update_language_info_error)
            }
        }
    }

    private fun handleCurrentWork() {
        if (mCurrentWorkChk.isChecked) {
            mEndDateTxt.text.clear()
            mEndDateContainer.visibility = View.GONE
        } else {
            mEndDateContainer.visibility = View.VISIBLE
        }
    }

    private fun removeExperience() {
        infoReference.child(experienceKey).removeValue().addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.remove_experience_error)
        }
    }

    private fun validateForm(): Boolean {
        clearError(mExperienceTitleTxt)
        clearError(mExperienceCompanyTxt)
        clearError(mStartDateTxt)
        clearError(mEndDateTxt)

        var isValid = true

        if (Utils.isNullOrWhiteSpace(mExperienceTitleTxt.text.toString())) {
            showError(mExperienceTitleTxt, R.string.experience_title_required)
            isValid = false
        }

        if (Utils.isNullOrWhiteSpace(mExperienceCompanyTxt.text.toString())) {
            showError(mExperienceCompanyTxt, R.string.experience_company_required)
            isValid = false
        }

        if (Utils.isNullOrWhiteSpace(mStartDateTxt.text.toString())) {
            showError(mStartDateTxt, R.string.start_required)
            isValid = false
        }

        if (!mCurrentWorkChk.isChecked
            && Utils.isNullOrWhiteSpace(mEndDateTxt.text.toString())) {
            showError(mEndDateTxt, R.string.end_required)
            isValid = false
        }

        return isValid
    }
}
