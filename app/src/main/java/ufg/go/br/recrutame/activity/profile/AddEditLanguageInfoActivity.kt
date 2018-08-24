package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.enum.EnumLanguageLevel
import ufg.go.br.recrutame.model.UserLanguageInfo

class AddEditLanguageInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mAddEditLanguageContainer
    private lateinit var mLanguageNameTxt: EditText
    private lateinit var mProficiencySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_language_info)
        inicializeControls()
    }

    override fun saveInfo() {
        val infoReference = mDatabase.child("users/$userId/languages")
        val newLanguage = infoReference.push()

        val generalInfo = UserLanguageInfo(
                mLanguageNameTxt.text.toString(),
                getEnumProficiency(mProficiencySpinner.selectedItem.toString()))
        newLanguage.setValue(generalInfo).addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.update_general_info_error)
        }
    }

    override fun getActionBarTitle(): String {
        return intent.getStringExtra("title")
    }

    private fun inicializeControls() {
        mLanguageNameTxt = findViewById(R.id.mLanguageNameTxt)

        mProficiencySpinner = findViewById(R.id.mProficiencySpinner)
        val proficiencies = mutableListOf<String>()
        EnumLanguageLevel.values().forEach {
            proficiencies.add(getString(it.idString))
        }

        val adapter = ArrayAdapter<String>(this, R.layout.custom_simple_spinner_item, proficiencies)
        mProficiencySpinner.adapter = adapter
        mProficiencySpinner.background = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_material)
    }

    private fun getEnumProficiency(proficiencyDescription: String): EnumLanguageLevel? {
        EnumLanguageLevel.values().forEach {
            if (getString(it.idString) == proficiencyDescription) {
                return it
            }
        }

        return null
    }
}
