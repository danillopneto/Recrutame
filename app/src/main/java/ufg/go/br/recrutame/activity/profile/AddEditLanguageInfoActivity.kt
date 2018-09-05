package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.enum.EnumLanguageLevel
import ufg.go.br.recrutame.model.UserLanguageInfo
import ufg.go.br.recrutame.util.Utils

class AddEditLanguageInfoActivity : EditProfileActivity(), View.OnClickListener {
    override var layoutId: Int = R.id.mAddEditLanguageContainer

    private lateinit var mLanguageNameTxt: EditText
    private lateinit var mProficiencySpinner: Spinner
    private var languageKey: String = ""
    private lateinit var mRemoveLanguageBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        languageKey = intent.getStringExtra("languageKey")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_language_info)
        inicializeControls()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mRemoveLanguageBtn -> {
                handleYesNoDialog(R.string.really_remove_language) { removeLanguage() }
            }
        }
    }

    override fun getActionBarTitle(): String {
        return if (languageKey.isEmpty()) {
            getString(R.string.add_language)
        } else {
            getString(R.string.edit_language)
        }
    }

    override fun inicializeControls() {
        infoReference = mDatabase.child("users/$userId/languages")
        var levelIndex = 0

        mLanguageNameTxt = findViewById(R.id.mLanguageNameTxt)

        mProficiencySpinner = findViewById(R.id.mProficiencySpinner)
        val proficiencies = mutableListOf<String>()
        EnumLanguageLevel.values().forEach {
            proficiencies.add(getString(it.idString) )
            if (!languageKey.isEmpty()) {
                val languageLevel = intent.getStringExtra("languageLevel")
                if (it.toString() == languageLevel){
                    levelIndex = it.ordinal
                }
            }
        }

        val adapter = ArrayAdapter<String>(this, R.layout.custom_simple_spinner_item, proficiencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mProficiencySpinner.adapter = adapter
        mProficiencySpinner.background = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_material)

        if (!languageKey.isEmpty()) {
            val languageDescription = intent.getStringExtra("languageDescription")
            mLanguageNameTxt.setText(languageDescription)
            mProficiencySpinner.setSelection(levelIndex)

            mRemoveLanguageBtn = findViewById(R.id.mRemoveLanguageBtn)
            mRemoveLanguageBtn.setOnClickListener(this)
            mRemoveLanguageBtn.visibility = View.VISIBLE
        }
    }

    override fun saveInfo() {
        hideKeyboard()
        if (!validateForm()) {
            return
        }

        if (languageKey.isEmpty()) {
            val newLanguage = infoReference.push()
            val generalInfo = UserLanguageInfo(
                    newLanguage.key,
                    mLanguageNameTxt.text.toString(),
                    getEnumProficiency(mProficiencySpinner.selectedItem.toString()))
            newLanguage.setValue(generalInfo).addOnCompleteListener {
                finish()
            }.addOnFailureListener {
                showError(R.string.add_language_info_error)
            }
        } else {
            val generalInfo = UserLanguageInfo(
                    languageKey,
                    mLanguageNameTxt.text.toString(),
                    getEnumProficiency(mProficiencySpinner.selectedItem.toString()))
            infoReference.child(languageKey).setValue(generalInfo).addOnCompleteListener {
                finish()
            }.addOnFailureListener {
                showError(R.string.update_language_info_error)
            }
        }
    }

    private fun getEnumProficiency(proficiencyDescription: String): EnumLanguageLevel? {
        EnumLanguageLevel.values().forEach {
            if (getString(it.idString) == proficiencyDescription) {
                return it
            }
        }

        return null
    }

    private fun removeLanguage() {
        infoReference.child(languageKey).removeValue().addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.remove_language_error)
        }
    }

    private fun validateForm(): Boolean {
        clearError(mLanguageNameTxt)

        if (Utils.isNullOrWhiteSpace(mLanguageNameTxt.text.toString())) {
            showError(mLanguageNameTxt, R.string.language_name_required)
            return false
        } else if (mProficiencySpinner.selectedItem == null
            || Utils.isNullOrWhiteSpace(mProficiencySpinner.selectedItem.toString())) {
            showError(R.string.proficiency_required)
            return false
        }

        return true
    }
}
