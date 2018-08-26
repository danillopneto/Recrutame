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
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.api.model.CityInfo
import ufg.go.br.recrutame.api.model.UFInfo
import ufg.go.br.recrutame.api.service.IBGEService
import ufg.go.br.recrutame.api.service.ServiceGenerator
import ufg.go.br.recrutame.util.IBGE_BASE_URL
import android.widget.AdapterView

class EditGeneralInfoActivity : EditProfileActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    override var layoutId: Int = R.id.mGeneralInfoLayout
    private lateinit var ibgeService: IBGEService
    private lateinit var mNameTxt: EditText
    private lateinit var mLastNameTxt: EditText
    private lateinit var mBirthdateTxt: EditText
    private lateinit var mGenderSpinner: Spinner
    private lateinit var mStateSpinner: Spinner
    private lateinit var mCitySpinner: Spinner
    private lateinit var states: List<UFInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_general_info)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mBirthdateTxt -> handleDatePicker()
            R.id.mStateSpinner -> syncCities(getStateData(mStateSpinner.selectedItem.toString()))
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
                                          mCitySpinner.selectedItem.toString())
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
        ibgeService = ServiceGenerator(IBGE_BASE_URL).createService(IBGEService::class.java)

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

        mGenderSpinner = findViewById(R.id.mGenderSpinner)
        val genders = resources.getStringArray(R.array.genders)
        val gender = intent.getStringExtra("userGender")
        setSpinnerConfig(mGenderSpinner, genders, gender)

        mStateSpinner = findViewById(R.id.mStateSpinner)
        syncStates()
        mStateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (mStateSpinner.selectedItem != null) {
                    syncCities(getStateData(mStateSpinner.selectedItem.toString()))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        mCitySpinner = findViewById(R.id.mCitySpinner)
    }

    private fun fillCities(cities: List<CityInfo>?) {
        if (cities == null) {
            return
        }

        val citiesName = mutableListOf<String>()
        cities.forEach { citiesName.add(it.nome) }
        citiesName.sort()

        val city = intent.getStringExtra("userCity")
        setSpinnerConfig(mCitySpinner, citiesName.toTypedArray(), city)
    }

    private fun fillStates() {
        val statesName = mutableListOf<String>()
        states.forEach { statesName.add(it.nome) }
        statesName.sort()

        val state = intent.getStringExtra("userState")
        setSpinnerConfig(mStateSpinner, statesName.toTypedArray(), state)
        if (mStateSpinner.selectedItem != null) {
            syncCities(getStateData(mStateSpinner.selectedItem.toString()))
        }
    }

    private fun getStateData(state: String): UFInfo? {
        states.forEach { if (it.nome == state) return it }
        return null
    }

    private fun syncCities(state: UFInfo?) {
        if (state == null) {
            return
        }

        ibgeService.getCities(state.id).enqueue(object : Callback<List<CityInfo>> {
            override fun onResponse(call: Call<List<CityInfo>>, response: Response<List<CityInfo>>) {
                val statusCode = response.code()
                if (statusCode == 200) {
                    fillCities(response.body())
                } else {
                    Toast.makeText(applicationContext, getString(R.string.get_cities_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CityInfo>>, t: Throwable) {
                Toast.makeText(applicationContext, getString(R.string.get_cities_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun syncStates() {
        ibgeService.getUFs().enqueue(object : Callback<List<UFInfo>> {
            override fun onResponse(call: Call<List<UFInfo>>, response: Response<List<UFInfo>>) {
                val statusCode = response.code()
                if (statusCode == 200) {
                    states = response.body()!!
                    fillStates()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.get_states_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UFInfo>>, t: Throwable) {
                Toast.makeText(applicationContext, getString(R.string.get_states_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
