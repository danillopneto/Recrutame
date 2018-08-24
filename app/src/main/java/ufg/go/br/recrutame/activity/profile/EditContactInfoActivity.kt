package ufg.go.br.recrutame.activity.profile

import android.os.Bundle
import android.widget.EditText
import ufg.go.br.recrutame.model.UserContactInfo
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.util.PhoneMaskUtil


class EditContactInfoActivity : EditProfileActivity() {
    override var layoutId: Int = R.id.mContactInfoLayout
    private lateinit var mEmailTxt: EditText
    private lateinit var mWebsiteTxt: EditText
    private lateinit var mPhoneTxt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact_info)
        inicializeControls()
    }

    override fun saveGeneralInfo() {
        val contactInfoReference = mDatabase.child("users/$userId/contactInfo")
        val contactInfo = UserContactInfo(
                                          mEmailTxt.text.toString(),
                                          mWebsiteTxt.text.toString(),
                                          mPhoneTxt.text.toString(),
                                          "")
        contactInfoReference.setValue(contactInfo).addOnCompleteListener {
            finish()
        }.addOnFailureListener {
            showError(R.string.update_contact_info_error)
        }
    }

    override fun getActionBarTitle(): String {
        return getString(R.string.edit_contact)
    }

    private fun inicializeControls() {
        val email = intent.getStringExtra("userEmail")
        mEmailTxt = findViewById(R.id.mEmailTxt)
        mEmailTxt.setText(email)

        val website = intent.getStringExtra("userWebsite")
        mWebsiteTxt = findViewById(R.id.mWebsiteTxt)
        mWebsiteTxt.setText(website)

        val phone = intent.getStringExtra("userPhone")
        mPhoneTxt = findViewById(R.id.mPhoneTxt)
        mPhoneTxt.addTextChangedListener(PhoneMaskUtil.insert(mPhoneTxt))
        mPhoneTxt.setText(phone)
    }
}
