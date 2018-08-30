package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.model.UserExperienceInfo
import ufg.go.br.recrutame.util.Utils
import java.util.*

class AddEditExperienceInfoActivity : EditProfileActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    override var layoutId: Int = R.id.mAddEditExperienceContainer

    private lateinit var infoReference: DatabaseReference
    private lateinit var mExperienceTitleTxt: EditText
    private lateinit var mExperienceCompanyTxt: EditText
    private lateinit var mStartDateTxt: EditText
    private lateinit var mEndDateTxt: EditText
    private lateinit var mCurrentWorkChk: CheckBox
    private lateinit var mRemoveExperienceBtn: Button
    private var experienceKey: String = ""
    private var dateEdited: Int? = null

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

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (dateEdited != null) {
            val formatedDate = Utils.getFormatedDate(year, monthOfYear + 1, dayOfMonth, getString(R.string.format_date_no_day))
            findViewById<EditText>(dateEdited!!).setText(formatedDate)
        }
    }

    override fun getActionBarTitle(): String {
        return if (experienceKey.isEmpty()) {
            getString(R.string.add_experience)
        } else {
            getString(R.string.edit_experience)
        }
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
            mEndDateTxt.visibility = View.GONE
        } else {
            mEndDateTxt.visibility = View.VISIBLE
        }
    }

    private fun handleDatePicker(input: EditText) {
        dateEdited = input.id
        val calendar = Calendar.getInstance()
        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)

        if (!input.text.isEmpty()) {
            val fullDate = Utils.getFullDateFromMonthYear(input.text.toString())
            month = Utils.getMonth(fullDate.toString()).toInt() - 1
            year = Utils.getYear(fullDate.toString()).toInt()
        }

        SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.DatePickerSpinner)
                .showDaySpinner(false)
                .defaultDate(year, month, 1)
                .build()
                .show()
    }

    private fun inicializeControls() {
        infoReference = mDatabase.child("users/$userId/experiences")

        mExperienceTitleTxt = findViewById(R.id.mExperienceTitleTxt)
        mExperienceCompanyTxt = findViewById(R.id.mExperienceCompanyTxt)

        mStartDateTxt = findViewById(R.id.mStartDateTxt)
        mStartDateTxt.setOnClickListener(this)

        mEndDateTxt = findViewById(R.id.mEndDateTxt)
        mEndDateTxt.setOnClickListener(this)

        mCurrentWorkChk = findViewById(R.id.mCurrentWorkChk)
        mCurrentWorkChk.setOnClickListener(this)

        if (!experienceKey.isEmpty()) {
            mExperienceTitleTxt.setText(intent.getStringExtra("experienceTitle"))
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

    private fun removeExperience() {
        infoReference.child(experienceKey).removeValue().addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.remove_experience_error)
        }
    }

    private fun validateForm(): Boolean {
        if (Utils.isNullOrWhiteSpace(mExperienceTitleTxt.text.toString())) {
            showError(R.string.experience_title_required)
            return false
        } else if (Utils.isNullOrWhiteSpace(mExperienceCompanyTxt.text.toString())) {
            showError(R.string.experience_company_required)
            return false
        } else if (Utils.isNullOrWhiteSpace(mStartDateTxt.text.toString())) {
            showError(R.string.experience_start_required)
            return false
        } else if (!mCurrentWorkChk.isChecked
            && Utils.isNullOrWhiteSpace(mEndDateTxt.text.toString())) {
            showError(R.string.experience_end_required)
            return false
        }

        return true
    }
}
