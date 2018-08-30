package ufg.go.br.recrutame.activity.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ufg.go.br.recrutame.R
import ufg.go.br.recrutame.activity.TabActivity
import ufg.go.br.recrutame.activity.BaseActivity
import ufg.go.br.recrutame.util.Utils
import ufg.go.br.recrutame.model.api.LIProfileInfo
import ufg.go.br.recrutame.model.UserContactInfo

abstract class LoginActivity : BaseActivity() {
    lateinit var liProfileInfo: LIProfileInfo
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: DatabaseReference
    var mActionButton: CircularProgressButton? = null
    var progressBar: ProgressBar? = null
    val profileUpdates = UserProfileChangeRequest.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inicializeApis()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, TabActivity:: class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mActionButton?.dispose()
    }

    fun insertUserData(userId: String, email: String, action: () -> Unit) {
        val contactInfoReference = mDatabase.child("users/$userId/contactInfo")
        val contactInfo = UserContactInfo(email, "", "", "")
        contactInfoReference.setValue(contactInfo).addOnCompleteListener {
            action()
        }
    }

    fun handleLogin(email: String, password: String, isNewUser: Boolean, isLIUser: Boolean) {
        getMyPreferences().setIsNewUser(isNewUser)
        getMyPreferences().setIsLIUser(isLIUser)
        hideKeyboard()
        if (!Utils.isNullOrWhiteSpace(email) && !Utils.isNullOrWhiteSpace(password)) {
            showLoading()
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                hideLoading()
                if (!task.isSuccessful) {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
                    } catch (e: FirebaseNetworkException) {
                        Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                        Log.e(ufg.go.br.recrutame.util.TAG, e.message)
                    }
                } else {
                    this.finishAffinity()
                    startActivity(Intent(this, TabActivity:: class.java))
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_credential), Toast.LENGTH_LONG).show()
        }
    }

    fun setNewUserData(displayName: String, photoUri: String) {
        profileUpdates.setDisplayName(displayName)
        profileUpdates.setPhotoUri(Uri.parse(photoUri))
    }

    fun inicializeApis() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
    }

    private fun hideLoading() {
        if (mActionButton != null) {
            mActionButton?.revertAnimation()
        } else if (progressBar != null) {
            progressBar?.visibility = View.INVISIBLE
        }
    }

    private fun showLoading() {
        if (mActionButton != null) {
            mActionButton?.startAnimation()
        } else if (progressBar != null) {
            progressBar?.visibility = View.VISIBLE
        }
    }
}
