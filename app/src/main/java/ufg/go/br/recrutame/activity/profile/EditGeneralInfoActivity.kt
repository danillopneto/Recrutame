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
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.api.model.UFInfo
import ufg.go.br.recrutame.api.service.LocaleService
import ufg.go.br.recrutame.api.service.ServiceGenerator
import ufg.go.br.recrutame.util.IBGE_BASE_URL

class EditGeneralInfoActivity : EditProfileActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    override var layoutId: Int = R.id.mGeneralInfoLayout
    private lateinit var mNameTxt: EditText
    private lateinit var mLastNameTxt: EditText
    private lateinit var mBirthdateTxt: EditText
    private lateinit var mGenderSpinner: AppCompatSpinner
    private lateinit var mStateSpinner: AppCompatSpinner
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

    override fun saveInfo() {
        val generalInfoReference = mDatabase.child("users/$userId/generalInfo")
        val generalInfo = UserGeneralInfo(
                                          mNameTxt.text.toString(),
                                          mLastNameTxt.text.toString(),
                                          Utils.getFullDate(mBirthdateTxt.text.toString()),
                                          mGenderSpinner.selectedItem.toString(),
                                          mStateSpinner.selectedItem.toString(),
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
        val genderAdapter = ArrayAdapter<String>(this, R.layout.custom_simple_spinner_item, genders)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mGenderSpinner.adapter = genderAdapter
        mGenderSpinner.background = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_material)
        if (!gender.isEmpty()) {
            val position = genderAdapter.getPosition(gender)
            mGenderSpinner.setSelection(position)
        }

        mStateSpinner = findViewById(R.id.mStateSpinner)
        val client = ServiceGenerator(IBGE_BASE_URL).createService(LocaleService::class.java)
        val call = client.getUFs()
        call.enqueue(object : Callback<List<UFInfo>> {
            override fun onResponse(call: Call<List<UFInfo>>, response: Response<List<UFInfo>>) {
                val statusCode = response.code()
                if (statusCode == 200) {
                    val states = response.body()
                    if (states != null) {
                        fillStates(states)
                    }
                } else {
                    Toast.makeText(applicationContext, getString(R.string.get_states_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UFInfo>>, t: Throwable) {
                Toast.makeText(applicationContext, getString(R.string.get_states_failed), Toast.LENGTH_SHORT).show()
            }
        })

        val city = intent.getStringExtra("userCity")
        mCityTxt = findViewById(R.id.mCityTxt)
        mCityTxt.setText(city)
    }

    private fun fillStates(states: List<UFInfo>) {
        val statesName = mutableListOf<String>()
        states.forEach { statesName.add(it.nome) }
        statesName.sort()

        val statesAdapter = ArrayAdapter<String>(this, R.layout.custom_simple_spinner_item, statesName)
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mStateSpinner.adapter = statesAdapter
        mStateSpinner.background = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_material)

        val state = intent.getStringExtra("userState")
        if (!state.isEmpty()) {
            val position = statesAdapter.getPosition(state)
            mStateSpinner.setSelection(position)
        }
    }
}
