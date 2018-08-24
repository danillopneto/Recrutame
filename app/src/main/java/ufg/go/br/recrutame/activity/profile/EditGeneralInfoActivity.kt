package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import ufg.go.br.recrutame.model.UserGeneralInfo
import ufg.go.br.recrutame.util.Utils
import java.util.*
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatSpinner
import android.widget.ArrayAdapter
import ufg.go.br.recrutame.R

class EditGeneralInfoActivity : EditProfileActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    override var layoutId: Int = R.id.mGeneralInfoLayout
    private lateinit var mNameTxt: EditText
    private lateinit var mLastNameTxt: EditText
    private lateinit var mBirthdateTxt: EditText
    private lateinit var mGenderSpinner: AppCompatSpinner
    private lateinit var mStateTxt: EditText
    private lateinit var mCityTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_general_info)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mBirthdateTxt -> handleDatePicker()
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mBirthdateTxt.setText(Utils.getFormatedDate(year, monthOfYear + 1, dayOfMonth, getString(R.string.format_date)))
    }

    override fun saveGeneralInfo() {
        val generalInfoReference = mDatabase.child("users/$userId/generalInfo")
        val generalInfo = UserGeneralInfo(
                                          mNameTxt.text.toString(),
                                          mLastNameTxt.text.toString(),
                                          Utils.getFullDate(mBirthdateTxt.text.toString()),
                                          mGenderSpinner.selectedItem.toString(),
                                          mStateTxt.text.toString(),
                                          mCityTxt.text.toString())
        generalInfoReference.setValue(generalInfo).addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.update_general_info_error)
        }
    }

    private fun handleDatePicker() {
        val calendar = Calendar.getInstance()
        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)

        if (!mBirthdateTxt.text.isEmpty()) {
            val fullDate = Utils.getFullDate(mBirthdateTxt.text.toString())
            dayOfMonth = Utils.getDayOfMonth(fullDate.toString()).toInt()
            month = Utils.getMonth(fullDate.toString()).toInt() - 1
            year = Utils.getYear(fullDate.toString()).toInt()
        }

        SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.DatePickerSpinner)
                .defaultDate(year, month, dayOfMonth)
                .build()
                .show()
    }

    private fun inicializeControls() {
        val name = intent.getStringExtra("userName")
        mNameTxt = findViewById(R.id.mNameTxt)
        mNameTxt.setText(name)

        val lastName = intent.getStringExtra("userLastName")
        mLastNameTxt = findViewById(R.id.mLastNameTxt)
        mLastNameTxt.setText(lastName)

        mBirthdateTxt = findViewById(R.id.mBirthdateTxt)
        mBirthdateTxt.setOnClickListener(this)
        val birthDate = intent.getIntExtra("userBirthdate", 0)
        if (birthDate > 0) {
            mBirthdateTxt.setText(Utils.getFormatedDate(birthDate, getString(R.string.format_date)))
        }

        val gender = intent.getStringExtra("userGender")
        mGenderSpinner = findViewById(R.id.mGenderSpinner)
        val genders = resources.getStringArray(R.array.genders)
        val adapter = ArrayAdapter<String>(this, R.layout.custom_simple_spinner_item, genders)
        mGenderSpinner.adapter = adapter
        mGenderSpinner.background = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_material)
        if (!gender.isEmpty()) {
            val position = adapter.getPosition(gender)
            mGenderSpinner.setSelection(position)
        }

        val state = intent.getStringExtra("userState")
        mStateTxt = findViewById(R.id.mStateTxt)
        mStateTxt.setText(state)

        val city = intent.getStringExtra("userCity")
        mCityTxt = findViewById(R.id.mCityTxt)
        mCityTxt.setText(city)
    }
}
