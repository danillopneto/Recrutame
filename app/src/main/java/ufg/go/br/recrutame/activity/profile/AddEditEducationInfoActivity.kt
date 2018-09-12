package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserEducationInfo
import ufg.go.br.recrutame.util.Utils

class AddEditEducationInfoActivity : EditProfileActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    override var layoutId: Int = R.id.mAddEditExperienceContainer

    private lateinit var mDegreeTxt: EditText
    private lateinit var mSchoolTxt: EditText
    private lateinit var mStartDateTxt: EditText
    private lateinit var mEndDateContainer: TextInputLayout
    private lateinit var mEndDateTxt: EditText
    private lateinit var mRemoveEducationBtn: Button
    private var educationKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        educationKey = intent.getStringExtra("educationKey")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_education_info)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mStartDateTxt -> handleDatePicker(mStartDateTxt)
            R.id.mEndDateTxt -> handleDatePicker(mEndDateTxt)
            R.id.mRemoveEducationBtn -> {
                handleYesNoDialog(R.string.really_remove_education) { removeEducation() }
            }
        }
    }

    override fun getActionBarTitle(): String {
        return if (educationKey.isEmpty()) {
            getString(R.string.add_education)
        } else {
            getString(R.string.edit_education)
        }
    }

    override fun inicializeControls() {
        infoReference = mDatabase.child("users/$userId/educations")

        mSchoolTxt = findViewById(R.id.mSchoolTxt)
        mDegreeTxt = findViewById(R.id.mDegreeTxt)

        mStartDateTxt = findViewById(R.id.mStartDateTxt)
        mStartDateTxt.setOnClickListener(this)

        mEndDateContainer = findViewById(R.id.mEndDateContainer)
        mEndDateTxt = findViewById(R.id.mEndDateTxt)
        mEndDateTxt.setOnClickListener(this)

        if (!educationKey.isEmpty()) {
            mSchoolTxt.setText(intent.getStringExtra("educationSchool"))
            mDegreeTxt.setText(intent.getStringExtra("educationDegree"))
            val startDate = intent.getIntExtra("educationStartDate", 0)
            if (startDate > 0) {
                mStartDateTxt.setText(Utils.getFormatedDate(startDate, getString(R.string.format_date_no_day)))
            }

            val endDate = intent.getIntExtra("educationEndDate", 0)
            if (endDate > 0) {
                mEndDateTxt.setText(Utils.getFormatedDate(endDate, getString(R.string.format_date_no_day)))
            }

            mRemoveEducationBtn = findViewById(R.id.mRemoveEducationBtn)
            mRemoveEducationBtn.setOnClickListener(this)
            mRemoveEducationBtn.visibility = View.VISIBLE
        }
    }

    override fun saveInfo() {
        hideKeyboard()
        if (!validateForm()) {
            return
        }

        if (educationKey.isEmpty()) {
            val newEducation = infoReference.push()
            val data = UserEducationInfo(
                    newEducation.key,
                    mSchoolTxt.text.toString(),
                    mDegreeTxt.text.toString(),
                    Utils.getFullDateFromMonthYear(mStartDateTxt.text.toString()),
                    Utils.getFullDateFromMonthYear(mEndDateTxt.text.toString()))
            newEducation.setValue(data).addOnCompleteListener {
                finish()
            }.addOnFailureListener {
                showError(R.string.add_language_info_error)
            }
        } else {
            val data = UserEducationInfo(
                    educationKey,
                    mSchoolTxt.text.toString(),
                    mDegreeTxt.text.toString(),
                    Utils.getFullDateFromMonthYear(mStartDateTxt.text.toString()),
                    Utils.getFullDateFromMonthYear(mEndDateTxt.text.toString()))
            infoReference.child(educationKey).setValue(data).addOnCompleteListener {
                finish()
            }.addOnFailureListener {
                showError(R.string.update_education_info_error)
            }
        }
    }

    private fun removeEducation() {
        infoReference.child(educationKey).removeValue().addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.remove_education_error)
        }
    }

    private fun validateForm(): Boolean {
        clearError(mDegreeTxt)
        clearError(mSchoolTxt)
        clearError(mStartDateTxt)
        clearError(mEndDateTxt)

        var isValid = true

        if (Utils.isNullOrWhiteSpace(mDegreeTxt.text.toString())) {
            showError(mDegreeTxt, R.string.degree_required)
            isValid = false
        }

        if (Utils.isNullOrWhiteSpace(mSchoolTxt.text.toString())) {
            showError(mSchoolTxt, R.string.school_required)
            isValid = false
        }

        if (Utils.isNullOrWhiteSpace(mStartDateTxt.text.toString())) {
            showError(mStartDateTxt, R.string.start_required)
            isValid = false
        }

        if (Utils.isNullOrWhiteSpace(mEndDateTxt.text.toString())) {
            showError(mEndDateTxt, R.string.end_required)
            isValid = false
        }

        return isValid
    }
}
